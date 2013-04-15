/**
 * This class has been generated using Gormas2Magentix tool.
 * 
 * @author Mario Rodrigo - mrodrigo@dsic.upv.es
 * 
 */
package EMFGormas_Example;

public class Constants {

    static enum AccessibilityType {
        EXTERNAL("external"), INTERNAL("internal");

        private final String value;

        private AccessibilityType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    static enum PositionType {
        CREATOR("creator"), MEMBER("member"), SUPERVISOR("supervisor"), SUBORDINATE("subordinate");

        private final String value;

        private PositionType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    static enum VisibilityType {
        PUBLIC("public"), PRIVATE("private");

        private final String value;

        private VisibilityType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    static enum UnitType {
        FLAT("flat"), TEAM("team"), HIERARCHY("hierarchy");

        private final String value;

        private UnitType(String value) {
            this.value = value;
        }

    }

}
