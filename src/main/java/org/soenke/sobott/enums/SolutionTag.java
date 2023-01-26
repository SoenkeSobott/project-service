package org.soenke.sobott.enums;

public enum SolutionTag {

    Shaft("shaft"),
    AnchorToExistingWall("anchor to existing wall"),
    Basement("basement"),
    ChamferCorner("chamfer corner"),
    CircularWall("circular wall"),
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
    SlabPlusBeamInOnePour("slab + beam in one pour"),
    SpindleSupport("spindle support"),
    StraightWall("straight wall"),
    SteelFrame("steel frame"),
    Tank("tank"),
    TemporaryStructure("temporary structure"),
    Traveler("traveler"),
    TunnelSideWall("tunnel side wall"),
    TWall("t-wall"),
    HighQualityConcreteSurface("high quality concrete surface"),
    Underground("underground"),
    UtilityTunnel("utility tunnel"),
    WallPlusSlabInOnePour("wall + slab in one pour"),
    WallPost("wall post"),
    WallWithVoids("wall with voids"),
    SlabWithBeams("slab with beams"),
    InclinedSlab("inclined slab"),
    PortalBeam("portal beam"),
    WorkingPlatform("working platform"),
    SteelBeamSupport("steel beam support"),
    RoofSlab("roof slab"),
    FlatSlab("flat slab"),
    StructureConcreteBeams("strut concrete beams"),
    TimberPacker("timber packer"),
    InclinedFoundation("inclined foundation"),
    TimberBoxOut("timber box-out"),
    AnchorToExistingStructure("anchor to existing structure"),
    PreCastSlab("pre-cast slab"),
    Underpass("underpass"),
    UnderwaterTunnel("underwater tunnel"),
    MultiStoreyShoring("multi-storey shoring"),
    IntermediateSlab("intermediate slab"),
    IntermediateWall("intermediate wall"),
    DepressedRoad("depressed road"),
    CutAndCover("cut & cover"),
    HollowSlab("hollow slab");

    private final String value;

    SolutionTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
