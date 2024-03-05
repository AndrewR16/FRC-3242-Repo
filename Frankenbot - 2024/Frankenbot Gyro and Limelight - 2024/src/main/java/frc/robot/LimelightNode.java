package frc.robot;

/**
 * In the Limelight applicaiton, these values should be put in the ID Filters
 * field
 * according to our team color:
 * <ul>
 * <li>Blue: 1, 2, 6, 7, 14, 15, 16</li>
 * <li>Red: 4, 5, 9, 10, 11, 12, 13</li>
 * </ul>
 */
public class LimelightNode {
    // Alliance color checker
    protected static final boolean isTeamBlue = true;

    // Node information
    String name;
    int requiredHeading;
    double requiredArea;
    int tagId;

    public LimelightNode(String name, int requiredHeading, double requiredArea, int tagId) {
        this.name = name;
        this.requiredHeading = requiredHeading;
        this.requiredArea = requiredArea;
        this.tagId = tagId;
    }

    // TODO: Adjust area values for all nodes

    // Blue nodes
    private static LimelightNode blue_SourceRight = new LimelightNode("Source (Right)", 40, 0.5, 1);
    private static LimelightNode blue_SourceLeft = new LimelightNode("Source (Left)", 40, 0.5, 2);
    private static LimelightNode blue_Amplifier = new LimelightNode("Amplifier", 90, 0.5, 6);
    private static LimelightNode blue_Speaker = new LimelightNode("Speaker", 0, 0.5, 7);

    // Red nodes
    private static LimelightNode red_SourceRight = new LimelightNode("Source (Right)", 310, 0.5, 9);
    private static LimelightNode red_SourceLeft = new LimelightNode("Source (Left)", 310, 0.5, 10);
    private static LimelightNode red_Amplifier = new LimelightNode("Amplifier", 270, 0.5, 5);
    private static LimelightNode red_Speaker = new LimelightNode("Speaker (Center)", 0, 0.5, 4);

    // Node list
    private static LimelightNode[] nodeList = { blue_SourceRight, blue_SourceLeft, blue_Amplifier, blue_Speaker, red_SourceRight, red_SourceLeft, red_Amplifier, red_Speaker };

    // Find node from id
    protected static String getNodeName(int tagId) {
        for (LimelightNode node : nodeList) {
            if (node.tagId == tagId) {
                return node.name;
            }
        }

        return "Id not found";
    }

    protected static int getNodeHeading(int tagId) {
        for (LimelightNode node : nodeList) {
            if (node.tagId == tagId) {
                return node.requiredHeading;
            }
        }

        return -1;
    }

    protected static double getNodeArea(int tagId) {
        for (LimelightNode node : nodeList) {
            if (node.tagId == tagId) {
                return node.requiredArea;
            }
        }

        return -1;
    }
}
