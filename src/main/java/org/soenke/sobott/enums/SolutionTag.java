package org.soenke.sobott.enums;

public enum SolutionTag {

    HighQualityConcreteSurface("high quality concrete surface"),
    Shaft("shaft"),
    AFrame("a-frame"),
    AnchorToExistingWall("anchor to existing wall"),
    Basement("basement"),
    ChamferCorner("chamfer corner"),
    CircularWall("circular wall"),
    ColumWithTieRodAndNonTieRodVersion("colum with tie-rod and non tie-rod version"),
    ColumnWithoutTieRod("column w/o tie-rod"),
    ColumnWithTieRod("column with tie-rod"),
    ConcreteShoringBeam("concrete shoring beam"),
    DoubleSided("double-sided"),
    DrainageChannel("drainage channel"),
    EquipmentFoundation("equipment foundation"),
    Inclined("inclined"),
    MockUp("mock-up"),
    Monolithic("monolithic"),
    SingleSided("single-sided"),
    SlabAndBeamInOnePour("slab & beam in one pour"),
    SludgePumpTank("sludge pump tank"),
    SpindleSupport("spindle support"),
    StraightWall("straight wall"),
    Tank("tank"),
    TemporaryStructure("temporary structure"),
    Traveler("traveler"),
    TunnelSideWall("tunnel side wall"),
    TWall("t-wall"),
    Underground("underground"),
    UtilityTunnel("utility tunnel"),
    WallAndSlabInOnePour("wall & slab in one pour"),
    WallPost("wall post"),
    WallWithVoids("wall with voids");

    private final String value;

    SolutionTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
