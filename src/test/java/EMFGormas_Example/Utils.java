/**
 * This class has been generated using Gormas2Magentix tool.
 * 
 * @author Mario Rodrigo - mrodrigo@dsic.upv.es
 * 
 */
package EMFGormas_Example;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

/**
 * Utilities class for manage everything related to Units, Roles, etc. in
 * THOMAS.
 * 
 * @author mrodrigo
 * 
 */
public class Utils {

	/**
	 * Class to save what roles are played by an Agent. These roles are saved as
	 * pairs (RoleID, UnitID).
	 * 
	 * @author mrodrigo
	 * 
	 */
	static class UnitRolePair {

		// ----------------------------------------
		// FIELDS of the class
		// ----------------------------------------
		private String roleID;
		private String unitID;

		// ----------------------------------------
		// CONSTRUCTOR of the class
		// ----------------------------------------
		/**
		 * Constructor of the class.
		 * 
		 * @param unitID
		 * @param roleID
		 */
		public UnitRolePair(String roleID, String unitID) {
			this.roleID = roleID.toLowerCase();
			this.unitID = unitID.toLowerCase();

		}

		// ----------------------------------------
		// METHODS of the class
		// ----------------------------------------
		/**
		 * @return the roleName
		 */
		public String getRoleID() {
			return roleID;
		}

		/**
		 * @param roleID
		 *            the roleName to set
		 */
		public void setRoleID(String roleID) {
			this.roleID = roleID;
		}

		/**
		 * @return the unitName
		 */
		public String getUnitID() {
			return unitID;
		}

		/**
		 * @param unitID
		 *            the unitName to set
		 */
		public void setUnitID(String unitID) {
			this.unitID = unitID;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((roleID == null) ? 0 : roleID.hashCode());
			result = prime * result
					+ ((unitID == null) ? 0 : unitID.hashCode());
			return result;
		}

		/*
		 * Compare current UnitRolePair with specified UnitRolePair
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof UnitRolePair))
				return false;
			UnitRolePair other = (UnitRolePair) obj;
			if (roleID == null) {
				if (other.roleID != null)
					return false;
			} else if (!roleID.equals(other.roleID))
				return false;
			if (unitID == null) {
				if (other.unitID != null)
					return false;
			} else if (!unitID.equals(other.unitID))
				return false;
			return true;
		}

	} // End of class PlayingRole

	/**
	 * Class to mantain local data about what roles are played and or create by
	 * an Agent. It also contains a list with the started agents and another
	 * with the created units.
	 * 
	 * @author mrodrigo
	 * 
	 */
	static class LocalData {

		// ----------------------------------------
		// FIELDS of the class
		// ----------------------------------------
		/**
		 * ArrayList to save the {@link UnitRolePair}s played by each Agent.
		 * This ArrayList will be used to properly leave them at shutdown time.
		 */
		private ArrayList<UnitRolePair> playingRoles;

		/**
		 * ArrayList to save the {@link UnitRolePair}s created by each Agent.
		 * This ArrayList will be used to properly deregister them at shutdown
		 * time.
		 */
		private ArrayList<UnitRolePair> createdRoles;

		/**
		 * ArrayList to save the {@link CAgents} started by each Agent. This
		 * ArrayList will be used to properly shutdown them at shutdown time.
		 */
		private ArrayList<CAgent> startedAgents;

		/**
		 * ArrayList to save the Units started by each Agent. This ArrayList
		 * will be used to properly deregister them at shutdown time.
		 */
		private ArrayList<String> createdUnits;

		/**
		 * ArrayList to save the services registered by each Agent. This
		 * ArrayList will be used to properly deregister them at shutdown time.
		 */
		private ArrayList<String> registeredServices;

		// ----------------------------------------
		// CONSTRUCTOR of the class
		// ----------------------------------------
		/**
		 * Constructor of the class
		 */
		public LocalData() {

			if (playingRoles == null) {
				playingRoles = new ArrayList<UnitRolePair>();
			}

			if (createdRoles == null) {
				createdRoles = new ArrayList<UnitRolePair>();
			}

			if (startedAgents == null) {
				startedAgents = new ArrayList<CAgent>();
			}

			if (createdUnits == null) {
				createdUnits = new ArrayList<String>();
			}

			if (registeredServices == null) {
				registeredServices = new ArrayList<String>();
			}
		}

		// ----------------------------------------
		// METHODS of the class
		// ----------------------------------------
		/**
		 * Method to get an ArrayList with the roles that the agent is playing.
		 * The first position in the array corresponds to the last role
		 * acquired.
		 * 
		 * @return the playingRoles.
		 */
		protected ArrayList<UnitRolePair> getPlayingRolesInReverseOrder() {
			ArrayList<UnitRolePair> reverseCollection = playingRoles;
			Collections.reverse(reverseCollection);
			return reverseCollection;

		}

		/**
		 * Method to get an ArrayList with the roles that the agent is playing.
		 * 
		 * @return the playingRoles.
		 */
		protected ArrayList<UnitRolePair> getPlayingRoles() {
			return playingRoles;
		}

		/**
		 * Method to add the provided {@link UnitRolePair} as parameter to the
		 * ArrayList of roles that this Agent is playing.
		 * 
		 * @param playingRole
		 *            the playingRole to add.
		 */
		protected void addPlayingRole(UnitRolePair playingRole) {
			if (playingRole != null) {
				this.playingRoles.add(playingRole);
			}
		}

		/**
		 * Method to retrieve the created roles in reverse order. The first in
		 * list is the last created.
		 * 
		 * @return the createdRoles.
		 */
		protected ArrayList<UnitRolePair> getCreatedRolesInReverseOrder() {
			ArrayList<UnitRolePair> reverseCollection = createdRoles;
			Collections.reverse(reverseCollection);
			return reverseCollection;
		}

		/**
		 * Method to retrieve the created roles inside the unitID provided as
		 * parameter. The first in the list is the last created.
		 * 
		 * @param unitID
		 * @return the created roles in this unit.
		 */
		protected ArrayList<String> getCreatedRolesInUnit(String unitID) {
			ArrayList<String> roles = new ArrayList<String>();
			for (UnitRolePair unitRolePair : createdRoles) {
				if (unitRolePair.unitID.equalsIgnoreCase(unitID)) {
					roles.add(unitID);
				}
			}
			// Change the order, the first will be the last created.
			Collections.reverse(roles);

			return roles;
		}

		/**
		 * Method to retrieve the created roles .
		 * 
		 * @return the createdRoles.
		 */
		protected ArrayList<UnitRolePair> getCreatedRoles() {
			return createdRoles;
		}

		/**
		 * Method to add the provided {@link UnitRolePair} as parameter to the
		 * ArrayList of roles that this Agent has created.
		 * 
		 * @param createdRole
		 *            the createdRole to add.
		 */
		protected void addCreatedRole(UnitRolePair createdRole) {
			if (createdRole != null) {
				this.createdRoles.add(createdRole);
			}
		}

		/**
		 * Method to retrieve the started Agents list in reverse order. The
		 * first in list is the last started.
		 * 
		 * @return the startedAgents.
		 */
		protected ArrayList<CAgent> getStartedAgentsInReverseOrder() {
			ArrayList<CAgent> reverseCollection = startedAgents;
			Collections.reverse(reverseCollection);
			return reverseCollection;
		}

		/**
		 * Method to retrieve the started Agents list.
		 * 
		 * @return the startedAgents.
		 */
		protected ArrayList<CAgent> getStartedAgents() {
			return startedAgents;
		}

		/**
		 * Method to add the provided {@link CAgent} as parameter to the
		 * ArrayList of agents that this Agent has started.
		 * 
		 * @param startedAgent
		 *            the startedAgent to add.
		 */
		protected void addStartedAgent(CAgent startedAgent) {
			if (startedAgent != null) {
				this.startedAgents.add(startedAgent);
			}
		}

		/**
		 * Method to retrieve the created Units list in reverse order. The first
		 * in this list is the last created.
		 * 
		 * @return the createdUnits
		 */
		protected ArrayList<String> getCreatedUnitsInReverseOrder() {
			ArrayList<String> reverseCollection = createdUnits;
			Collections.reverse(reverseCollection);
			return reverseCollection;
		}

		/**
		 * Method to retrieve the created Units list.
		 * 
		 * @return the createdUnits
		 */
		protected ArrayList<String> getCreatedUnits() {
			return createdUnits;
		}

		/**
		 * Method to add the provided {@link CAgent} as parameter to the
		 * ArrayList of agents that this Agent has started.
		 * 
		 * @param startedAgent
		 *            the startedAgent to add.
		 */
		protected void addCreatedUnit(String createdUnit) {
			if (createdUnit != null) {
				this.createdUnits.add(createdUnit);
			}
		}

		/**
		 * Method to add the provided service as parameter to the ArrayList of
		 * services that this Agent has registered.
		 * 
		 * @param registeredService
		 *            the registeredServices to add
		 */
		protected void addRegisteredServices(String registeredService) {
			if (registeredService != null) {
				this.registeredServices.add(registeredService);
			}
		}

		/**
		 * Method to retrieve the registered Services list.
		 * @return the registeredServices
		 */
		protected ArrayList<String> getRegisteredServices() {
			return registeredServices;
		}

	} // End of class LocalData

	// ----------------------------------------
	// METHODS of the class
	// ----------------------------------------
	/**
	 * Method to get an ArrayList with the roles that the agent is playing. The
	 * first position in the array corresponds to the last role acquired.
	 * 
	 * @param searchedAgent
	 * @param omsProxy
	 * @return ArrayList<PlayingRole>
	 */
	protected static ArrayList<UnitRolePair> queryOMSForRolesPlayedByAnAgent(
			CAgent searchedAgent, OMSProxy omsProxy, Logger logger) {

		ArrayList<ArrayList<String>> agentRoles;
		ArrayList<UnitRolePair> agentPlayingRoles = new ArrayList<UnitRolePair>();
		UnitRolePair playingRole;

		try {
			agentRoles = omsProxy.informAgentRole(searchedAgent.getName());

			for (ArrayList<String> pair : agentRoles) {
				String roleName = pair.get(0);
				String unitName = pair.get(1);
				if (roleName != null && unitName != null) {
					playingRole = new UnitRolePair(roleName, unitName);
					agentPlayingRoles.add(playingRole);
				}
			}

		} catch (THOMASException e) {
			// This exception is launched if service can not found the agent.
			// In this case, the web service provides an error with
			// this description: "Not found. The agent <Agent Name> not exists".
			// If description is diferent, we show the stack trace. In other
			// cases,
			// the execution can continue.
			if (e.getMessage()
					.contains(searchedAgent.getName() + " not exists")) {
				logger.warn("[" + searchedAgent.getName() + "] "
						+ e.getMessage());

			} else {
				logger.error("[" + searchedAgent.getName() + "] "
						+ e.getMessage());
			}
		}

		// This method lets reverse the order of elements of
		// agentPlayingRoles. So, the agent can leave the roles in inverse
		// order that he acquires them.
		Collections.reverse(agentPlayingRoles);
		return agentPlayingRoles;
	}

} // End of class Utils
