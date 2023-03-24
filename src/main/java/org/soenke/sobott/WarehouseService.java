package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.soenke.sobott.entity.Article;
import org.soenke.sobott.entity.ArticleAvailableQuantity;
import org.soenke.sobott.entity.SubstituteArticle;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class WarehouseService implements PanacheMongoRepository<Article> {

    Logger LOGGER = Logger.getLogger(WarehouseService.class.getName());

    public Response searchArticles(String searchTerm) {
        List<Article> articles;
        if (searchTerm == null || searchTerm.isEmpty()) {
            articles = Article.getFirstArticles(1000);
        } else {
            articles = Article.searchArticles(searchTerm);
        }
        return Response.status(Response.Status.OK).entity(articles).build();
    }

    public Response updateArticleAvailability(String articleNumber, Integer newAvailability) {
        if (newAvailability == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Availability empty").build();
        }
        Article articleInDB = Article.findByArticleNumber(articleNumber);
        if (articleInDB != null) {
            articleInDB.setAvailability(newAvailability);
            articleInDB.persistOrUpdate();
            return Response.status(Response.Status.OK).entity("Updated Article availability").build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build();
    }

    public Response getArticlesAvailability(String articleNumbers) {
        if (articleNumbers == null || articleNumbers.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No article numbers passed").build();
        }
        List<String> articleNumbersList = List.of(articleNumbers.split(","));
        List<ArticleAvailableQuantity> articleAvailabilityQuantities = new ArrayList<>();
        articleNumbersList.forEach(articleNumber -> {
            Article article = Article.findByArticleNumber(articleNumber);
            if (article != null) {
                articleAvailabilityQuantities.add(new ArticleAvailableQuantity(articleNumber, article.getListPrice(),
                        article.getAvailability()));
            }
        });

        return Response.status(Response.Status.OK).entity(articleAvailabilityQuantities).build();
    }

    public Response uploadWeeklyArticles(final Map<String, InputStream> parts) {
        if (parts.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No data passed in upload").build();
        }

        InputStream firstEntry = parts.get("file");
        if (firstEntry == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No file passed in upload").build();
        }

        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(firstEntry);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Uploaded file can't be read into Excel workbook: " + e.getMessage()).build();
        }

        Sheet sheet;
        try {
            sheet = ExcelUtil.generateValueCopiedSheetFromWeeklyArticlesExcel(workbook);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        Map<Integer, Map<String, String>> articlesData;
        try {
            articlesData = ExcelUtil.getDataFromWeeklyArticlesExcel(sheet);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("The Excel format is corrupted").build();
        }
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            try {
                i = uploadComplexArticleFromArticleData(articlesData, i);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Uploading Article with index " + i + " caused error: " + e);
            }
        }

        return Response.status(Response.Status.OK).entity("Transferred all Article data").build();
    }

    protected Integer uploadComplexArticleFromArticleData(Map<Integer, Map<String, String>> articlesData, Integer startIndex) {
        List<SubstituteArticle> substituteArticles = new ArrayList<>();
        Integer iterator = startIndex;
        while (iterator < Integer.MAX_VALUE) {
            Map<String, String> articleData = articlesData.get(iterator);
            String articleNumber = articleData.get("Article Number");
            if (articleNumber.contains("Total")) {
                for (SubstituteArticle child : substituteArticles) {
                    // Check if article already exists
                    String childArticleNumber = child.getArticleNumber();
                    Article articleInDB = Article.findByArticleNumber(childArticleNumber);
                    if (articleInDB != null) {
                        LOGGER.log(Level.INFO, "Article already exists, updating now: " + childArticleNumber);
                        articleInDB.setAvailability(child.getAvailability());
                        if (substituteArticles.size() > 1) {
                            articleInDB.setSubstituteArticles(substituteArticles);
                        }
                        articleInDB.persistOrUpdate();
                    } else {
                        LOGGER.log(Level.INFO, "New Article, uploading now: " + childArticleNumber);
                        Article newArticle = new Article();
                        newArticle.setArticleNumber(childArticleNumber);
                        newArticle.setWeight(0f);
                        newArticle.setListPrice(0f);
                        newArticle.setAvailability(child.getAvailability());
                        if (substituteArticles.size() > 1) {
                            newArticle.setSubstituteArticles(substituteArticles);
                        }
                        newArticle.persistOrUpdate();
                    }
                }
                break;
            } else {
                SubstituteArticle substituteArticle = new SubstituteArticle();
                substituteArticle.setArticleNumber(articleData.get("Article\nNew"));
                double availability = Double.parseDouble(articleData.get("Net availability"));
                if (availability >= 0) {
                    substituteArticle.setAvailability((int) Math.floor(availability));
                } else {
                    substituteArticle.setAvailability((int) Math.ceil(availability));
                }
                substituteArticles.add(substituteArticle);
            }
            iterator++;
        }
        return iterator;
    }

}
