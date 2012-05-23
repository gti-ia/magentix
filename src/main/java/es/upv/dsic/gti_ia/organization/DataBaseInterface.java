package es.upv.dsic.gti_ia.organization;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import es.upv.dsic.gti_ia.organization.THOMASMessages.MessageID;

class DataBaseInterface {
    private DataBaseAccess db;

    /**
     * Used for retrieve local messages.
     */
    private THOMASMessages l10n;

    DataBaseInterface() {
        db = new DataBaseAccess();
        l10n = new THOMASMessages();

    }

    String acquireRole(String unitName, String roleName, String agentName) throws SQLException {
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        ResultSet res = null;
        ResultSet res2 = null;
        Connection connection = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idUnit = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = " + idUnit);
                if (res2.next()) {
                    int idRole = res2.getInt("idroleList");
                    st3 = connection.createStatement();
                    int res3 = st3.executeUpdate("INSERT INTO agentPlayList (agentName, idroleList) VALUES ('" + agentName + "', " + idRole + ")");
                    if (res3 != 0) {
                        connection.commit();
                        return roleName + " acquired";
                    }
                }
                return "Error: role " + roleName + " not found in unit " + unitName;
            }
            return "Error: unit " + unitName + " not found in database";
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
        }
    }

    /*
     * String allocateRole(String roleName, String unitName, String
     * targetAgentName, String agentName) throws SQLException{ // TODO no té
     * molt de trellat la especificació, fa el mateix q l'anterior funció
     * Statement st; st = db.connection.createStatement(); ResultSet res =
     * st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+
     * unitName+"'"); if(res.next()){ int idUnit = res.getInt("idunitList");
     * Statement st2 = db.connection.createStatement(); ResultSet res2 =
     * st2.executeQuery
     * ("SELECT idroleList FROM roleList WHERE roleName ='"+roleName
     * +"' AND idunitList = "+idUnit); if(res2.next()){ int idRole =
     * res.getInt("idroleList"); Statement st3 =
     * db.connection.createStatement(); int res3 =st3.executeUpdate(
     * "INSERT INTO agentPlayList (agentName, idroleList) VALUES ('"
     * +agentName+"', "+idRole+")"); if(res3 != 0){ db.connection.commit();
     * return "<"+roleName+" + \"acquired\">"; } } return
     * "Error: role "+roleName+" not found in unit "+unitName; } return
     * "Error: unit "+unitName+" not found in database"; }
     */

    boolean checkAgent(String agentName) throws SQLException {
        boolean exists = false;
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connection = db.connect();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM agentPlayList WHERE agentName='" + agentName + "'");
            while (rs.next()) {

                exists = true;
            }

            connection.commit();
            return exists;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (stmt != null)
                stmt.close();

            if (rs != null)
                rs.close();
        }
    }

    boolean checkAgentInUnit(String agentName, String unit) throws SQLException {
        boolean exists = false;
        Connection connection = null;

        Statement stmt = null;
        Statement stmt2 = null;

        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;

        try {
            connection = db.connect();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
            if (rs.next()) {
                int unitId = rs.getInt("idunitList");
                rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + unitId);
                while (rs2.next()) {
                    stmt2 = connection.createStatement();
                    int idRole = rs2.getInt("idroleList");

                    rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList=" + idRole + " AND agentName='" + agentName + "'");

                    if (rs3.next()) {
                        exists = true;
                    }
                }
            }

            connection.commit();
            return exists;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (stmt != null)
                stmt.close();
            if (stmt2 != null)
                stmt2.close();

            if (rs != null)
                rs.close();
            if (rs2 != null)
                rs2.close();
            if (rs3 != null)
                rs3.close();
        }
    }

    boolean checkAgentPlaysRole(String agentName, String role, String unit) throws SQLException {
        boolean exists = false;
        Connection connection = null;
        Statement stmt = null;
        Statement stmt2 = null;

        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;

        try {
            connection = db.connect();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
            if (rs.next()) {
                int unitId = rs.getInt("idunitList");
                rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + unitId + " AND roleName ='" + role + "'");
                while (rs2.next()) {
                    int roleId = rs2.getInt("idroleList");
                    stmt2 = connection.createStatement();
                    rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = " + roleId + " AND agentName='" + agentName + "'");
                    if (rs3.next()) {

                        exists = true;
                    }
                }

            }
            connection.commit();
            return exists;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (stmt != null)
                stmt.close();
            if (stmt2 != null)
                stmt2.close();

            if (rs != null)
                rs.close();
            if (rs2 != null)
                rs2.close();
            if (rs3 != null)
                rs3.close();
        }
    }

    boolean checkNoCreatorAgentsInUnit(String unit) throws SQLException {

        Connection connection = null;
        Statement stmt = null;
        Statement stmt2 = null;

        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;

        try {
            connection = db.connect();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
            if (rs.next()) {
                int positionId = rs.getInt("idposition");
                rs2 = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
                if (rs2.next()) {
                    int unitId = rs2.getInt("idunitList");
                    rs3 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitlist =" + unitId + " AND idposition !=" + positionId);
                    while (rs3.next()) {

                        int roleId = rs3.getInt("idroleList");

                        stmt2 = connection.createStatement();
                        rs4 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList =" + roleId);

                        if (rs4.next()) {
                            connection.commit();
                            return true;
                        }
                    }
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (stmt != null)
                stmt.close();
            if (stmt2 != null)
                stmt2.close();

            if (rs != null)
                rs.close();
            if (rs2 != null)
                rs2.close();
            if (rs3 != null)
                rs3.close();
            if (rs4 != null)
                rs4.close();
        }
    }

    boolean checkPlayedRoleInUnit(String role, String unit) throws SQLException {

        Connection connection = null;
        Statement stmt = null;
        Statement stmt2 = null;

        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;

        try {
            connection = db.connect();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
            if (rs.next()) {
                int unitId = rs.getInt("idunitList");
                rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + unitId + " AND roleName='" + role + "'");
                if (rs2.next()) {
                    int roleId = rs2.getInt("idroleList");
                    stmt2 = connection.createStatement();
                    rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList =" + roleId);
                    if (rs3.next()) {
                        connection.commit();
                        return true;
                    }
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (stmt != null)
                stmt.close();
            if (stmt2 != null)
                stmt2.close();

            if (rs != null)
                rs.close();
            if (rs2 != null)
                rs2.close();
            if (rs3 != null)
                rs3.close();
        }
    }

    boolean checkTargetRoleNorm(String role, String unit) {
        // TODO on estan les normes
        return false;
    }

    boolean checkPosition(String agent, String position) throws SQLException {

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agent + "'");
            while (res.next()) {
                int idRole = res.getInt("idroleList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idposition FROM roleList WHERE idroleList =" + idRole);
                if (res2.next()) {
                    int idPosition = res2.getInt("idposition");
                    st3 = connection.createStatement();
                    res3 = st3.executeQuery("SELECT * FROM position WHERE idposition =" + idPosition);
                    if (res3.next() && res3.getString("position").equalsIgnoreCase(position)) {
                        connection.commit();
                        return true;
                    }
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
        }
    }

    boolean checkPositionInUnit(String agent, String position, String unit) throws SQLException {

        int idUnit = -1;

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agent + "'");
            st4 = connection.createStatement();
            res4 = st4.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
            if (res4.next())
                idUnit = res4.getInt("idunitList");
            while (res.next() && idUnit > -1) {
                int idRole = res.getInt("idroleList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idposition FROM roleList WHERE idroleList =" + idRole + " AND idunitList =" + idUnit);
                if (res2.next()) {
                    int idPosition = res2.getInt("idposition");
                    st3 = connection.createStatement();
                    res3 = st3.executeQuery("SELECT * FROM position WHERE idposition =" + idPosition);
                    if (res3.next() && res3.getString("position").equalsIgnoreCase(position)) {
                        connection.commit();
                        return true;
                    }
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();
        }
    }

    boolean checkRole(String role, String unit) throws SQLException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
            if (res.next()) {
                int idUnit = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList =" + idUnit + " AND roleName ='" + role + "'");
                if (res2.next()) {
                    connection.commit();
                    return true;
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    boolean checkSubUnits(String unit) throws SQLException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'");
            if (res.next()) {
                int idUnit = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT * FROM unitHierarchy WHERE idParentUnit =" + idUnit);
                if (res2.next()) {
                    connection.commit();
                    return true;
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    boolean checkUnit(String unit) throws SQLException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT * FROM unitList WHERE unitName ='" + unit + "'");
            if (res.next()) {
                connection.commit();
                return true;
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    boolean checkVirtualUnit(String unit) throws SQLException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitType FROM unitType WHERE unitTypeName ='virtual'");
            if (res.next()) {
                int idUnitType = res.getInt("idunitType");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT * FROM unitList WHERE idunitType =" + idUnitType + " AND unitName ='" + unit + "'");
                if (res2.next()) {
                    connection.commit();
                    return true;
                }
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    String createRole(String roleName, String unitName, String accessibility, String visibility, String position) throws SQLException, InvalidAccessibilityException, InvalidVisibilityException, InvalidPositionException, UnitNotExistsException {

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res4 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunit = res.getInt("idunitList");
                st4 = connection.createStatement();
                res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='" + position + "'");
                if (res4.next()) {
                    int idposition = res4.getInt("idposition");
                    st5 = connection.createStatement();
                    res5 = st5.executeQuery("SELECT idaccesibility FROM accesibility WHERE accesibility ='" + accessibility + "'");
                    if (res5.next()) {
                        int idaccesibility = res5.getInt("idaccesibility");
                        st6 = connection.createStatement();
                        res6 = st6.executeQuery("SELECT idvisibility FROM visibility WHERE visibility ='" + visibility + "'");
                        if (res6.next()) {
                            int idvisibility = res6.getInt("idvisibility");
                            st3 = connection.createStatement();
                            int res3 = st3.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccesibility, idvisibility) VALUES ('" + roleName + "', " + idunit + "," + idposition + "," + idaccesibility + "," + idvisibility + ")");
                            if (res3 != 0) {
                                connection.commit();
                                return roleName + " created";
                            }
                        }
                        String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, visibility);
                        throw new InvalidVisibilityException(message);
                    }
                    String message = l10n.getMessage(MessageID.INVALID_ACCESSIBILITY, accessibility);
                    throw new InvalidAccessibilityException(message);
                }
                String message = l10n.getMessage(MessageID.INVALID_POSITION, position);
                throw new InvalidPositionException(message);
            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;
        } catch (InvalidAccessibilityException e) {
            throw e;
        } catch (InvalidPositionException e) {
            throw e;
        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res4 != null)
                res4.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();

        }
    }

    String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorAgentName) throws SQLException, InsertingTableException, InvalidVisibilityException, InvalidAccessibilityException, InvalidPositionException, ParentUnitNotExistsException, InvalidUnitTypeException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st8 = null;
        Statement st9 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res5 = null;
        ResultSet res7 = null;
        ResultSet res8 = null;
        ResultSet res9 = null;
        ResultSet res12 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitType FROM unitType WHERE unitTypeName ='" + unitType + "'");
            if (res.next()) {
                int idunitType = res.getInt("idunitType");
                st4 = connection.createStatement();
                int res4 = st4.executeUpdate("INSERT INTO unitList (unitName, idunitType) VALUES ('" + unitName + "', " + idunitType + ")");
                if (res4 != 0) {
                    st12 = connection.createStatement();
                    res12 = st12.executeQuery("SELECT LAST_INSERT_ID()");
                    if (res12.next()) {
                        int insertedUnitId = res12.getInt(1);
                        st5 = connection.createStatement();
                        res5 = st5.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + parentUnitName + "'");
                        if (res5.next()) {
                            int idunitParent = res5.getInt("idunitList");
                            st6 = connection.createStatement();
                            int res6 = st6.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES (" + idunitParent + ", " + insertedUnitId + ")");
                            if (res6 != 0) {
                                st7 = connection.createStatement();
                                res7 = st7.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
                                if (res7.next()) {
                                    int idposition = res7.getInt("idposition");
                                    st8 = connection.createStatement();
                                    res8 = st8.executeQuery("SELECT idaccesibility FROM accesibility WHERE accesibility ='internal'");
                                    if (res8.next()) {
                                        int idaccesibility = res8.getInt("idaccesibility");
                                        st9 = connection.createStatement();
                                        res9 = st9.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
                                        if (res9.next()) {
                                            int idVisibility = res9.getInt("idVisibility");
                                            st10 = connection.createStatement();
                                            int res10 = st10.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccesibility, idvisibility) VALUES ('" + creatorAgentName + "', " + insertedUnitId + ", " + idposition + "," + idaccesibility + "," + idVisibility + ")");
                                            if (res10 != 0) {
                                                st11 = connection.createStatement();
                                                int res11 = st11.executeUpdate("INSERT INTO agentPlayList (agentName, idroleList) VALUES ('" + agentName + "', LAST_INSERT_ID())");
                                                if (res11 != 0) {
                                                    connection.commit();
                                                    return unitName + " created";
                                                }
                                                String message = l10n.getMessage(MessageID.INSERTING_TABLE, "agentPlayList");
                                                throw new InsertingTableException(message);

                                            }
                                            String message = l10n.getMessage(MessageID.INSERTING_TABLE, "roleList");
                                            throw new InsertingTableException(message);

                                        }
                                        String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                                        throw new InvalidVisibilityException(message);

                                    }
                                    String message = l10n.getMessage(MessageID.INVALID_ACCESSIBILITY, "internal");
                                    throw new InvalidAccessibilityException(message);

                                }
                                String message = l10n.getMessage(MessageID.INVALID_POSITION, "creator");
                                throw new InvalidPositionException(message);

                            }
                            String message = l10n.getMessage(MessageID.INSERTING_TABLE, "unitHierarchy");
                            throw new InsertingTableException(message);

                        }
                        String message = l10n.getMessage(MessageID.PARENT_UNIT_NOT_EXISTS, parentUnitName);
                        throw new ParentUnitNotExistsException(message);
                    }
                }
                String message = l10n.getMessage(MessageID.INSERTING_TABLE, "unitList");
                throw new InsertingTableException(message);

            }
            String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
            throw new InvalidUnitTypeException(message);

        } catch (SQLException e) {
            throw e;

        } catch (InsertingTableException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (InvalidAccessibilityException e) {
            throw e;

        } catch (InvalidPositionException e) {
            throw e;

        } catch (ParentUnitNotExistsException e) {
            throw e;

        } catch (InvalidUnitTypeException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st8 != null)
                st8.close();
            if (st9 != null)
                st9.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res5 != null)
                res5.close();
            if (res7 != null)
                res7.close();
            if (res8 != null)
                res8.close();
            if (res9 != null)
                res9.close();
            if (res12 != null)
                res12.close();

        }
    }

    String deallocateRole(String roleName, String unitName, String targetAgentName, String agentName) throws SQLException, MySQLException, RoleNotExistsException, UnitNotExistsException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList =" + idunitList);
                if (res2.next()) {
                    int idroleList = res2.getInt("idroleList");
                    st3 = connection.createStatement();
                    int res3 = st3.executeUpdate("DELETE FROM agentPlayList WHERE agentName = '" + targetAgentName + "' AND idroleList = " + idroleList);
                    if (res3 != 0) {
                        connection.commit();
                        return roleName + " deallocated";
                    }
                    String message = l10n.getMessage(MessageID.MYSQL, res3);
                    throw new MySQLException(message);

                }
                String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, roleName);
                throw new RoleNotExistsException(message);

            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (MySQLException e) {
            throw e;

        } catch (RoleNotExistsException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    String deleteRole(String roleName, String unitName, String agentName) throws SQLException, MySQLException, UnitNotExistsException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;

        ResultSet res = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                int res2 = st2.executeUpdate("DELETE FROM roleList WHERE roleName ='" + roleName + "' AND idunitList =" + idunitList);
                if (res2 != 0) {
                    connection.commit();
                    return roleName + " deleted";
                }
                String message = l10n.getMessage(MessageID.MYSQL, res2);
                throw new MySQLException(message);

            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (MySQLException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();

            if (res != null)
                res.close();

        }
    }

    String deleteUnit(String unitName, String agentName) throws SQLException, DeletingTableException, InvalidPositionException, UnitNotExistsException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
                if (res2.next()) {
                    int idposition = res2.getInt("idposition");
                    st3 = connection.createStatement();
                    res3 = st3.executeQuery("SELECT idroleList FROM roleList WHERE idposition =" + idposition + " AND idunitList =" + idunitList);
                    while (res3.next()) {
                        int idroleList = res3.getInt("idroleList");
                        st4 = connection.createStatement();
                        st4.executeUpdate("DELETE FROM agentPlayList WHERE idroleList =" + idroleList);
                        // if(res4 == 0)//Puede que no haya nadie jugando ese
                        // rol, no tiene por que dar un error.
                        // throw new
                        // THOMASException("Error: mysql error in agentPlayList "+res4);
                    }
                    st5 = connection.createStatement();
                    st5.executeUpdate("DELETE FROM roleList WHERE idunitList =" + idunitList);
                    st7 = connection.createStatement();
                    int res7 = st7.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit =" + idunitList);
                    if (res7 != 0) {
                        st6 = connection.createStatement();
                        int res6 = st6.executeUpdate("DELETE FROM unitList WHERE idunitList =" + idunitList);
                        if (res6 != 0) {
                            connection.commit();
                            return unitName + " deleted";
                        }
                        String message = l10n.getMessage(MessageID.DELETING_TABLE, "unitList");
                        throw new DeletingTableException(message);

                    }
                    String message = l10n.getMessage(MessageID.DELETING_TABLE, "unitHierarchy");
                    throw new DeletingTableException(message);

                }
                String message = l10n.getMessage(MessageID.INVALID_POSITION, "creator");
                throw new InvalidPositionException(message);

            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (DeletingTableException e) {
            throw e;

        } catch (InvalidPositionException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();

        }
    }

    String jointUnit(String unitName, String parentName) throws SQLException, MySQLException, ParentUnitNotExistsException, UnitNotExistsException {

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + parentName + "'");
                if (res2.next()) {
                    int idParentUnit = res2.getInt("idunitList");
                    st3 = connection.createStatement();
                    int res3 = st3.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit =" + idunitList);
                    if (res3 != 0) {
                        st4 = connection.createStatement();
                        int res4 = st4.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES (" + idParentUnit + "," + idunitList + ")");
                        if (res4 != 0) {
                            connection.commit();
                            return unitName + " + jointed to " + parentName;
                        }
                    }
                    String message = l10n.getMessage(MessageID.MYSQL, res3);
                    throw new MySQLException(message);

                }
                String message = l10n.getMessage(MessageID.PARENT_UNIT_NOT_EXISTS, parentName);
                throw new ParentUnitNotExistsException(message);

            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (MySQLException e) {
            throw e;

        } catch (ParentUnitNotExistsException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    String leaveRole(String unitName, String roleName, String agentName) throws SQLException, MySQLException, RoleNotExistsException, UnitNotExistsException {

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "'");
                if (res2.next()) {
                    int idroleList = res2.getInt("idroleList");
                    st3 = connection.createStatement();
                    int res3 = st3.executeUpdate("DELETE FROM agentPlayList WHERE idroleList =" + idroleList + " AND agentName='" + agentName + "'");
                    if (res3 != 0) {
                        connection.commit();
                        return roleName + " left";
                    }
                    String message = l10n.getMessage(MessageID.MYSQL, res3);
                    throw new MySQLException(message);

                }
                String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, roleName, unitName);
                throw new RoleNotExistsException(message);

            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (MySQLException e) {
            throw e;

        } catch (RoleNotExistsException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    String getUnitType(String unitName) throws SQLException, IDUnitTypeNotFoundException, UnitNotExistsException {
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;

        ResultSet res = null;
        ResultSet res2 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitType FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitType = res.getInt("idunitType");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType =" + idunitType);
                if (res2.next()) {
                    connection.commit();
                    return res2.getString("unitTypeName");
                }
                String message = l10n.getMessage(MessageID.ID_UNIT_TYPE_NOT_FOUND, idunitType);
                throw new IDUnitTypeNotFoundException(message);

            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (IDUnitTypeNotFoundException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();

        }
    }

    ArrayList<ArrayList<String>> getAgentsInUnit(String unitName) throws SQLException, UnitNotExistsException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList =" + idunitList);
                while (res2.next()) {
                    ArrayList<String> aux = new ArrayList<String>();
                    String roleName = res2.getString("roleName");
                    int idposition = res2.getInt("idposition");
                    int idroleList = res2.getInt("idroleList");
                    st3 = connection.createStatement();
                    res3 = st3.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
                    if (res3.next())
                        aux.add(res3.getString("agentName"));
                    aux.add(roleName);
                    st4 = connection.createStatement();
                    res4 = st4.executeQuery("SELECT position FROM position WHERE idposition =" + idposition);
                    if (res4.next())
                        aux.add(res4.getString("position"));
                    result.add(aux);
                }
                connection.commit();
                return result;
            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;
        } catch (UnitNotExistsException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();

        }
    }

    ArrayList<String> getParentsUnit(String unitName) throws SQLException, UnitNotExistsException {

        ArrayList<String> result = new ArrayList<String>();

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next()) {
                int idunitList = res.getInt("idunitList");
                st2 = connection.createStatement();
                res2 = st2.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit =" + idunitList);
                if (res2.next()) {
                    int idParentUnit = res2.getInt("idParentUnit");
                    st3 = connection.createStatement();
                    res3 = st3.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + idParentUnit);
                    if (res3.next()) {
                        result.add(res3.getString("unitName"));
                        connection.commit();
                        return result;
                    }
                }
                result.add("virtual");
                connection.commit();
                return result;
            }
            String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
            throw new UnitNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();

        }
    }

    ArrayList<ArrayList<String>> getInformAgentRole(String requestedAgentName, String agentName) throws SQLException, InvalidVisibilityException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st8 = null;
        Statement st9 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;
        Statement st13 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res8 = null;
        ResultSet res9 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;
        ResultSet res12 = null;
        ResultSet res13 = null;

        try {
            connection = db.connect();

            st2 = connection.createStatement();
            int idVisibility;
            res2 = st2.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res2.next())
                idVisibility = res2.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st = connection.createStatement();
            res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + requestedAgentName + "'");
            while (res.next()) {
                int idroleList = res.getInt("idroleList");
                st3 = connection.createStatement();
                res3 = st3.executeQuery("SELECT idunitList, roleName FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility =" + idVisibility);
                if (res3.next()) {
                    ArrayList<String> aux = new ArrayList<String>();
                    int idunitList = res3.getInt("idunitList");
                    String roleName = res3.getString("roleName");
                    st4 = connection.createStatement();
                    res4 = st4.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + idunitList);
                    if (res4.next()) {
                        String unitName = res4.getString("unitName");
                        aux.add(roleName);
                        aux.add(unitName);
                        result.add(aux);
                    }
                }
            }

            ArrayList<Integer> idunits1 = new ArrayList<Integer>();
            ArrayList<Integer> idunits2 = new ArrayList<Integer>();
            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res6.next())
                idVisibility = res6.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st7 = connection.createStatement();
            res7 = st7.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + requestedAgentName + "'");
            while (res7.next()) {
                int idroleList = res7.getInt("idroleList");
                st8 = connection.createStatement();
                res8 = st8.executeQuery("SELECT idunitList FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility =" + idVisibility);
                if (res8.next()) {
                    idunits1.add(res8.getInt("idunitList"));
                }
            }

            st9 = connection.createStatement();
            res9 = st9.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res9.next()) {
                int idroleList = res9.getInt("idroleList");
                st10 = connection.createStatement();
                res10 = st10.executeQuery("SELECT idunitList FROM roleList WHERE idroleList =" + idroleList);// AND
                // idvisibility
                // ="+idVisibility);
                if (res10.next()) {
                    idunits2.add(res10.getInt("idunitList"));
                }
            }

            for (int unitid : idunits1) {

                if (idunits2.contains(unitid)) {
                    st11 = connection.createStatement();
                    res11 = st11.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + requestedAgentName + "'");
                    while (res11.next()) {
                        ArrayList<String> aux = new ArrayList<String>();
                        int idroleList = res11.getInt("idroleList");
                        st12 = connection.createStatement();

                        res12 = st12.executeQuery("SELECT roleName FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility =" + idVisibility + " AND idunitList=" + unitid);
                        if (res12.next()) {
                            st13 = connection.createStatement();
                            res13 = st13.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + unitid);
                            if (res13.next()) {
                                aux.add(res12.getString("roleName"));
                                aux.add(res13.getString("unitName"));
                                result.add(aux);
                            }
                        }
                    }
                }
            }
            connection.commit();
            return result;

        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st8 != null)
                st8.close();
            if (st9 != null)
                st9.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();
            if (st13 != null)
                st13.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res8 != null)
                res8.close();
            if (res9 != null)
                res9.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
            if (res12 != null)
                res12.close();
            if (res13 != null)
                res13.close();

        }
    }

    ArrayList<ArrayList<String>> getInformAgentRolesPlayedInUnit(String unitName, String targetAgentName) throws SQLException, UnitNotExistsException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        int idunitList;

        Connection connection = null;

        Statement st2 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;
        Statement st13 = null;
        Statement st14 = null;

        ResultSet res2 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;
        ResultSet res12 = null;
        ResultSet res13 = null;
        ResultSet res14 = null;

        try {
            connection = db.connect();

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st10 = connection.createStatement();
            res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + targetAgentName + "'");
            while (res10.next()) {
                int idroleList = res10.getInt("idroleList");
                st11 = connection.createStatement();
                res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList + " AND idunitList=" + idunitList);
                if (res11.next()) {
                    int idvisibility = res11.getInt("idvisibility");
                    int idaccesibility = res11.getInt("idaccesibility");
                    int idposition = res11.getInt("idposition");
                    String roleName = res11.getString("roleName");
                    String position = "";
                    String visibility = "";
                    String accesibility = "";

                    st12 = connection.createStatement();
                    res12 = st12.executeQuery("SELECT * FROM position WHERE idposition =" + idposition);
                    if (res12.next())
                        position = res12.getString("position");

                    st13 = connection.createStatement();
                    res13 = st13.executeQuery("SELECT * FROM accesibility WHERE idaccesibility =" + idaccesibility);
                    if (res13.next())
                        accesibility = res13.getString("accesibility");

                    st14 = connection.createStatement();
                    res14 = st14.executeQuery("SELECT * FROM visibility WHERE idvisibility =" + idvisibility);
                    if (res14.next())
                        visibility = res14.getString("visibility");

                    ArrayList<String> aux = new ArrayList<String>();
                    aux.add(roleName);
                    aux.add(visibility);
                    aux.add(accesibility);
                    aux.add(position);
                    result.add(aux);
                }
            }
            connection.commit();
            return result;

        } catch (SQLException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st2 != null)
                st2.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();
            if (st13 != null)
                st13.close();

            if (res2 != null)
                res2.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
            if (res12 != null)
                res12.close();
            if (res13 != null)
                res13.close();

        }
    }

    ArrayList<ArrayList<String>> getAgentsRolesInUnit(String unitName, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        int idPublicVisibility;
        int idPrivateVisbility;
        boolean playsRole = false;
        int idunitList;
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;
        Statement st13 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;
        ResultSet res12 = null;
        ResultSet res13 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res3.next())
                idPrivateVisbility = res3.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st10 = connection.createStatement();
            res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res10.next()) {
                int idroleList = res10.getInt("idroleList");
                st11 = connection.createStatement();
                res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList + " AND idunitList=" + idunitList);
                if (res11.next()) {
                    playsRole = true;
                    break;
                }
            }

            st12 = connection.createStatement();

            if (playsRole)
                res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList =" + idunitList + " AND (idVisibility =" + idPrivateVisbility + " OR idVisibility =" + idPublicVisibility + ")");
            else
                res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList =" + idunitList + " AND idVisibility =" + idPublicVisibility);
            while (res12.next()) {

                String roleName = res12.getString("roleName");
                int idroleList = res12.getInt("idroleList");
                st13 = connection.createStatement();

                res13 = st13.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);

                while (res13.next()) {
                    ArrayList<String> aux = new ArrayList<String>();
                    aux.add(res13.getString("agentName"));
                    aux.add(roleName);
                    result.add(aux);
                }

            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();
            if (st13 != null)
                st13.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
            if (res12 != null)
                res12.close();
            if (res13 != null)
                res13.close();

        }
    }

    ArrayList<String> getAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException {
        ArrayList<String> result = new ArrayList<String>();
        int idPublicVisibility;
        int idPrivateVisbility;
        boolean playsRole = false;
        int idunitList;
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;
        Statement st13 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;
        ResultSet res12 = null;
        ResultSet res13 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res3.next())
                idPrivateVisbility = res3.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st10 = connection.createStatement();
            res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res10.next()) {
                int idroleList = res10.getInt("idroleList");
                st11 = connection.createStatement();
                res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList + " AND idunitList=" + idunitList);
                if (res11.next()) {
                    playsRole = true;
                    break;
                }
            }

            st12 = connection.createStatement();

            if (playsRole)
                res12 = st12.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "' AND (idVisibility =" + idPrivateVisbility + " OR idVisibility =" + idPublicVisibility + ")");
            else
                res12 = st12.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "' AND idVisibility =" + idPublicVisibility);
            while (res12.next()) {
                int idroleList = res12.getInt("idroleList");
                st13 = connection.createStatement();
                res13 = st13.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
                while (res13.next()) {
                    result.add(res13.getString("agentName"));
                }
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();
            if (st13 != null)
                st13.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
            if (res12 != null)
                res12.close();
            if (res13 != null)
                res13.close();

        }

    }

    ArrayList<ArrayList<String>> getAgentsPlayingPositionInUnit(String unitName, String positionValue, String agentName) throws SQLException, InvalidVisibilityException, InvalidPositionException, UnitNotExistsException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        int idposition;
        int idPublicVisibility;
        int idPrivateVisbility;
        boolean playsRole = false;
        int idunit;

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;
        Statement st13 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;
        ResultSet res12 = null;
        ResultSet res13 = null;

        try {
            connection = db.connect();

            st10 = connection.createStatement();
            res10 = st10.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res10.next())
                idPublicVisibility = res10.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }
            st11 = connection.createStatement();
            res11 = st11.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res11.next())
                idPrivateVisbility = res11.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }
            st = connection.createStatement();
            res = st.executeQuery("SELECT idposition FROM position WHERE position ='" + positionValue + "'");
            if (res.next())
                idposition = res.getInt("idposition");
            else {
                String message = l10n.getMessage(MessageID.INVALID_POSITION, positionValue);
                throw new InvalidPositionException(message);
            }
            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunit = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunit);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            st3 = connection.createStatement();

            if (playsRole) {
                res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList =" + idunit + " AND idposition =" + idposition + " AND (idvisibility =" + idPrivateVisbility + " OR idvisibility =" + idPublicVisibility + ")");
            } else {
                res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList =" + idunit + " AND idposition =" + idposition + " AND idVisibility =" + idPublicVisibility);
            }
            while (res3.next()) {
                int idroleList = res3.getInt("idroleList");
                String roleName = res3.getString("roleName");
                st4 = connection.createStatement();
                res4 = st4.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
                while (res4.next()) {
                    ArrayList<String> aux = new ArrayList<String>();
                    aux.add(res4.getString("agentName"));
                    aux.add(roleName);
                    result.add(aux);
                }
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (InvalidPositionException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();
            if (st13 != null)
                st13.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
            if (res12 != null)
                res12.close();
            if (res13 != null)
                res13.close();

        }
    }

    ArrayList<String> getAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException, RoleNotExistsException, InvalidPositionException {
        // TODO deurien tornarse els agents q juguen el role roleName, amb la
        // posicio positionValue en la unitat unitName?
        ArrayList<String> result = new ArrayList<String>();
        int idPublicVisibility;
        int idPrivateVisbility;
        int idposition;
        int idroleList;
        boolean playsRole = false;
        int idunitList;

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st10 = null;
        Statement st11 = null;
        Statement st12 = null;
        Statement st13 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;
        ResultSet res12 = null;
        ResultSet res13 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res3.next())
                idPrivateVisbility = res3.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st4 = connection.createStatement();
            res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='" + positionValue + "'");
            if (res4.next())
                idposition = res4.getInt("idposition");
            else {
                String message = l10n.getMessage(MessageID.INVALID_POSITION, positionValue);
                throw new InvalidPositionException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunitList);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            st5 = connection.createStatement();

            if (playsRole) {

                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idUnitList=" + idunitList + " AND roleName ='" + roleName + "' AND idposition =" + idposition + " AND (idvisibility =" + idPrivateVisbility + " OR idvisibility =" + idPublicVisibility + ")");
            } else {

                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idUnitList=" + idunitList + " AND roleName ='" + roleName + "' AND idposition =" + idposition + " AND idVisibility =" + idPublicVisibility);
            }
            if (res5.next())
                idroleList = res5.getInt("idroleList");
            else {
                String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, roleName);
                throw new RoleNotExistsException(message);
            }

            st10 = connection.createStatement();

            res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
            while (res10.next()) {

                result.add(res10.getString("agentName"));
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } catch (RoleNotExistsException e) {
            throw e;

        } catch (InvalidPositionException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();
            if (st12 != null)
                st12.close();
            if (st13 != null)
                st13.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
            if (res12 != null)
                res12.close();
            if (res13 != null)
                res13.close();

        }
    }

    int getQuantityAgentsRolesInUnit(String unitName, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException {
        int idPublicVisibility;
        int idroleList;
        boolean playsRole = false;
        int idunitList;
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st10 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res10 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunitList);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            Set<String> agentNames = new HashSet<String>();
            st5 = connection.createStatement();

            if (playsRole)
                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList=" + idunitList);
            else
                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList=" + idunitList + " AND idVisibility =" + idPublicVisibility);
            while (res5.next()) {
                idroleList = res5.getInt("idroleList");
                st10 = connection.createStatement();
                res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
                while (res10.next()) {
                    agentNames.add(res10.getString("agentName"));
                }
            }
            connection.commit();
            return agentNames.size();
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st10 != null)
                st10.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res10 != null)
                res10.close();
        }
    }

    int getQuantityAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException {
        int cont = 0;
        int idPublicVisibility;
        int idPrivateVisbility;
        int idroleList;
        boolean playsRole = false;
        int idunitList;
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st10 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res10 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res3.next())
                idPrivateVisbility = res3.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunitList);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            st5 = connection.createStatement();

            if (playsRole)
                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "' AND (idVisibility =" + idPrivateVisbility + " OR idvisibility =" + idPublicVisibility + ")");
            else
                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "' AND idvisibility =" + idPublicVisibility);
            if (res5.next())
                idroleList = res5.getInt("idroleList");
            else
                return 0;

            st10 = connection.createStatement();
            res10 = st10.executeQuery("SELECT DISTINCT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
            while (res10.next()) {
                cont++;
            }
            connection.commit();
            return cont;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st10 != null)
                st10.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res10 != null)
                res10.close();
        }
    }

    int getQuantityAgentsPlayingPositionInUnit(String unitName, String positionValue, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException, InvalidPositionException {
        int idPublicVisibility;
        int idposition;
        int idroleList;
        boolean playsRole = false;
        int idunitList;

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st10 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res4 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res10 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st4 = connection.createStatement();
            res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='" + positionValue + "'");
            if (res4.next())
                idposition = res4.getInt("idposition");
            else {
                String message = l10n.getMessage(MessageID.INVALID_POSITION, positionValue);
                throw new InvalidPositionException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunitList);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            Set<String> agentNames = new HashSet<String>();
            st5 = connection.createStatement();

            if (playsRole) {

                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList=" + idunitList + " AND idposition =" + idposition);
            } else {

                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList=" + idunitList + " AND idposition =" + idposition + " AND idvisibility =" + idPublicVisibility);
            }
            while (res5.next()) {
                idroleList = res5.getInt("idroleList");
                st10 = connection.createStatement();

                res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
                while (res10.next()) {

                    agentNames.add(res10.getString("agentName"));
                }
            }
            connection.commit();
            return agentNames.size();
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } catch (InvalidPositionException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st10 != null)
                st10.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res4 != null)
                res4.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res10 != null)
                res10.close();
        }
    }

    int getQuantityAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException, InvalidPositionException {
        int cont = 0;
        int idPublicVisibility;
        int idPrivateVisbility;
        int idposition;
        int idroleList;
        boolean playsRole = false;
        int idunitList;

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;
        Statement st6 = null;
        Statement st7 = null;
        Statement st10 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;
        ResultSet res5 = null;
        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res10 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res3.next())
                idPrivateVisbility = res3.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st4 = connection.createStatement();
            res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='" + positionValue + "'");
            if (res4.next())
                idposition = res4.getInt("idposition");
            else {
                String message = l10n.getMessage(MessageID.INVALID_POSITION, positionValue);
                throw new InvalidPositionException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunitList);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            st5 = connection.createStatement();

            if (playsRole)
                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "' AND idposition =" + idposition + " AND (idVisibility =" + idPrivateVisbility + " OR idvisibility =" + idPublicVisibility + ")");
            else
                res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList =" + idunitList + " AND roleName ='" + roleName + "' AND idposition =" + idposition + " AND idvisibility =" + idPublicVisibility);
            if (res5.next())
                idroleList = res5.getInt("idroleList");
            else
                return 0;

            st10 = connection.createStatement();
            res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList =" + idroleList);
            while (res10.next()) {
                cont++;
            }
            connection.commit();
            return cont;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } catch (InvalidPositionException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st10 != null)
                st10.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();
            if (res5 != null)
                res5.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res10 != null)
                res10.close();
        }
    }

    ArrayList<String> getInformUnit(String unitName) throws SQLException, UnitNotExistsException {
        ArrayList<String> result = new ArrayList<String>();
        int idunitType;
        int idunitList;

        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        Statement st5 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;
        ResultSet res4 = null;
        ResultSet res5 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res.next())
                idunitList = res.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitType FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitType = res2.getInt("idunitType");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType =" + idunitType);
            res3.next();

            result.add(res3.getString("unitTypeName"));
            st4 = connection.createStatement();
            res4 = st4.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit =" + idunitList);
            if (res4.next()) {
                st5 = connection.createStatement();
                res5 = st5.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + res4.getInt("idParentUnit"));
                res5.next();
                result.add(res5.getString("unitName"));
            } else
                result.add("");
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st4 != null)
                st4.close();
            if (st5 != null)
                st5.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res4 != null)
                res4.close();
            if (res5 != null)
                res5.close();
        }
    }

    ArrayList<ArrayList<String>> getInformUnitRoles(String unitName, String agentName) throws SQLException, InvalidVisibilityException, UnitNotExistsException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        int idPublicVisibility;
        int idPrivateVisbility;
        boolean playsRole = false;
        int idunitList;
        Connection connection = null;
        Statement st = null;
        Statement st2 = null;
        Statement st3 = null;

        Statement st6 = null;
        Statement st7 = null;
        Statement st8 = null;
        Statement st9 = null;
        Statement st10 = null;
        Statement st11 = null;

        ResultSet res = null;
        ResultSet res2 = null;
        ResultSet res3 = null;

        ResultSet res6 = null;
        ResultSet res7 = null;
        ResultSet res8 = null;
        ResultSet res9 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;

        try {
            connection = db.connect();

            st = connection.createStatement();
            res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
            if (res.next())
                idPublicVisibility = res.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "public");
                throw new InvalidVisibilityException(message);
            }

            st3 = connection.createStatement();
            res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
            if (res3.next())
                idPrivateVisbility = res3.getInt("idVisibility");
            else {
                String message = l10n.getMessage(MessageID.INVALID_VISIBILITY, "private");
                throw new InvalidVisibilityException(message);
            }

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='" + agentName + "'");
            while (res6.next()) {
                int idroleList2 = res6.getInt("idroleList");
                st7 = connection.createStatement();
                res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList=" + idunitList);
                if (res7.next()) {
                    playsRole = true;
                    break;
                }
            }

            st8 = connection.createStatement();

            if (playsRole)
                res8 = st8.executeQuery("SELECT roleName, idaccesibility, idvisibility, idposition FROM roleList WHERE idunitList =" + idunitList + " AND (idVisibility =" + idPrivateVisbility + " OR idVisibility =" + idPublicVisibility + ")");
            else
                res8 = st8.executeQuery("SELECT roleName, idaccesibility, idvisibility, idposition FROM roleList WHERE idunitList =" + idunitList + " AND idVisibility =" + idPublicVisibility);
            while (res8.next()) {
                ArrayList<String> aux = new ArrayList<String>();
                int idposition = res8.getInt("idposition");
                int idaccesibility = res8.getInt("idaccesibility");
                int idvisibility = res8.getInt("idvisibility");
                st9 = connection.createStatement();
                res9 = st9.executeQuery("SELECT position FROM position WHERE idposition =" + idposition);
                res9.next();

                st10 = connection.createStatement();
                res10 = st10.executeQuery("SELECT accesibility FROM accesibility WHERE idaccesibility =" + idaccesibility);
                res10.next();

                st11 = connection.createStatement();
                res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility =" + idvisibility);
                res11.next();
                aux.add(res8.getString("roleName"));
                aux.add(res10.getString("accesibility"));
                aux.add(res11.getString("visibility"));
                aux.add(res9.getString("position"));
                result.add(aux);
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw e;

        } catch (InvalidVisibilityException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();
            if (st != null)
                st.close();
            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st6 != null)
                st6.close();
            if (st7 != null)
                st7.close();
            if (st8 != null)
                st8.close();
            if (st9 != null)
                st9.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();

            if (res != null)
                res.close();
            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res6 != null)
                res6.close();
            if (res7 != null)
                res7.close();
            if (res8 != null)
                res8.close();
            if (res9 != null)
                res9.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
        }
    }

    ArrayList<String> getInformRole(String roleName, String unitName) throws SQLException, UnitNotExistsException, RoleNotExistsException {
        ArrayList<String> result = new ArrayList<String>();
        int idunitList;

        Connection connection = null;

        Statement st2 = null;
        Statement st3 = null;

        Statement st6 = null;

        Statement st9 = null;
        Statement st10 = null;
        Statement st11 = null;

        ResultSet res2 = null;
        ResultSet res3 = null;

        ResultSet res6 = null;

        ResultSet res9 = null;
        ResultSet res10 = null;
        ResultSet res11 = null;

        try {
            connection = db.connect();

            st2 = connection.createStatement();
            res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
            if (res2.next())
                idunitList = res2.getInt("idunitList");
            else {
                String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
                throw new UnitNotExistsException(message);
            }

            st6 = connection.createStatement();
            res6 = st6.executeQuery("SELECT idaccesibility, idposition, idvisibility FROM roleList WHERE roleName ='" + roleName + "' AND idunitList =" + idunitList);
            if (res6.next()) {
                int idposition = res6.getInt("idposition");
                int idaccesibility = res6.getInt("idaccesibility");
                int idvisibility = res6.getInt("idvisibility");
                st9 = connection.createStatement();
                res9 = st9.executeQuery("SELECT position FROM position WHERE idposition =" + idposition);
                res9.next();

                st10 = connection.createStatement();
                res10 = st10.executeQuery("SELECT accesibility FROM accesibility WHERE idaccesibility =" + idaccesibility);
                res10.next();

                st11 = connection.createStatement();
                res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility =" + idvisibility);
                res11.next();
                result.add(res10.getString("accesibility"));
                result.add(res11.getString("visibility"));
                result.add(res9.getString("position"));
                connection.commit();
                return result;
            }
            String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, roleName);
            throw new RoleNotExistsException(message);

        } catch (SQLException e) {
            throw e;

        } catch (UnitNotExistsException e) {
            throw e;

        } catch (RoleNotExistsException e) {
            throw e;

        } finally {
            if (connection != null)
                connection.close();

            if (st2 != null)
                st2.close();
            if (st3 != null)
                st3.close();
            if (st6 != null)
                st6.close();
            if (st9 != null)
                st9.close();
            if (st10 != null)
                st10.close();
            if (st11 != null)
                st11.close();

            if (res2 != null)
                res2.close();
            if (res3 != null)
                res3.close();
            if (res6 != null)
                res6.close();
            if (res9 != null)
                res9.close();
            if (res10 != null)
                res10.close();
            if (res11 != null)
                res11.close();
        }
    }
}
