package org.soenke.sobott.enums;

public enum SolutionTag {

    HighQualityConcreteSurface("High Quality Concrete Surface"),
    Shaft("Shaft"),
    AFrame("A-Frame"),
    AnchorToExistingWall("Anchor To Existing Wall"),
    Basement("Basement"),
    ChamferCorner("Chamfer Corner"),
    CircularWall("Circular Wall"),
    ColumWithTieRodAndNonTieRodVersion("Colum With Tie-Rod And Non Tie-Rod Version"),
    ColumnWithoutTieRod("Column W/o Tie-Rod"),
    ColumnWithTieRod("Column With Tie-Rod"),
    ConcreteShoringBeam("Concrete Shoring Beam"),
    DoubleSided("Double-Sided"),
    DrainageChannel("Drainage Channel"),
    EquipmentFoundation("Equipment Foundation"),
    Inclined("Inclined"),
    MockUp("Mock-Up"),
    Monolithic("Monolithic"),
    SingleSided("Single-Sided"),
    SlabAndBeamInOnePour("Slab & Beam In One Pour"),
    SludgePumpTank("Sludge Pump Tank"),
    SpindleSupport("Spindle Support"),
    StraightWall("Straight Wall"),
    Tank("Tank"),
    TemporaryStructure("Temporary Structure"),
    Traveler("Traveler"),
    TunnelSideWall("Tunnel Side Wall"),
    TWall("T-Wall"),
    Underground("Underground"),
    UtilityTunnel("Utility Tunnel"),
    WallAndSlabInOnePour("Wall & Slab In One Pour"),
    WallPost("Wall Post"),
    WallWithVoids("Wall With Voids");

    private final String value;

    SolutionTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
