/**
 * 
 */
package es.upv.dsic.gti_ia.organization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The class that contains the internationalized messages used in the
 * 
 * @author mrodrigo
 * 
 */
public class THOMASMessages {

    // -----------------------------------------------------------------
    // ENUMS of the class
    // -----------------------------------------------------------------

    /**
     * The identifiers of the messages.
     */
    public enum MessageID {
        /**
         * Exception message when Agent not exists.
         */
        AGENT_NOT_EXISTS,
        /**
         * Exception message when Agent is not in Unit.
         */
        AGENT_NOT_IN_UNIT,
        /**
         * Exception message when occurs and error trying to delete a value from
         * a table.
         */
        DELETING_TABLE,
        /**
         * Exception message when any parameter is empty.
         */
        EMPTY_PARAMETERS,
        /**
         * Exception message when occurs an exchange bind error.
         */
        EXCHANGE_BIND,
        /**
         * Exception message when occurs an exchange unbind error.
         */
        EXCHANGE_UNBIND,
        /**
         * Exception message when IDUnitType is not found.
         */
        ID_UNIT_TYPE_NOT_FOUND,
        /**
         * Exception message when occurs an error trying to insert a value in a
         * table.
         */
        INSERTING_TABLE,
        /**
         * Exception message when Accessibility value is incorrect.
         */
        INVALID_ACCESSIBILITY,
        /**
         * Exception message when Position value is incorrect.
         */
        INVALID_POSITION,
        /**
         * Exception message when Role Position value is incorrect.
         */
        INVALID_ROLE_POSITION,
        /**
         * Exception message when Unit Type value is incorrect.
         */
        INVALID_UNIT_TYPE,
        /**
         * Exception message when Visibility value is incorrect.
         */
        INVALID_VISIBILITY,
        /**
         * Exception message when occurs a MySQL error.
         */
        MYSQL,
        /**
         * Exception message when none of the agents in unit play roles with
         * position creator.
         */
        NOT_CREATOR_AGENT_IN_UNIT,
        /**
         * Exception message when the agent does not play any role with position
         * creator in the unit.
         */
        NOT_CREATOR,
        /**
         * Exception message when the agent does not play any role with position
         * creator inside the parent unit.
         */
        NOT_CREATOR_IN_PARENT_UNIT,
        /**
         * Exception message when the agent does not play any role with position
         * creator inside the unit.
         */
        NOT_CREATOR_IN_UNIT,
        /**
         * Exception message when the agent does not play any role with position
         * creator in the unit or the parent unit.
         */
        NOT_CREATOR_IN_UNIT_OR_PARENT_UNIT,
        /**
         * Exception message when the agent is not inside the unit and does not
         * play any role with position creator.
         */
        NOT_IN_UNIT_AND_NOT_CREATOR,
        /**
         * Exception message when the agent is not inside the unit or the parent
         * unit.
         */
        NOT_IN_UNIT_OR_PARENT_UNIT,
        /**
         * Exception message when the agent does not play any role with position
         * member or creator in unit.
         */
        NOT_MEMBER_OR_CREATOR_IN_UNIT,
        /**
         * Exception message when the agent does not play any role.
         */
        NOT_PLAYS_ANY_ROLE,
        /**
         * Exception message when the agent does not play the role.
         */
        NOT_PLAYS_ROLE,
        /**
         * Exception message when the agent does not play any role with position
         * supervisor or creator in unit.
         */
        NOT_SUPERVISOR_OR_CREATOR_IN_UNIT,
        /**
         * Exception message when the agent is only playing the role creator.
         */
        ONLY_PLAYS_CREATOR,
        /**
         * Exception message when the parent unit does not exists.
         */
        PARENT_UNIT_NOT_EXISTS,
        /**
         * Exception message when the agent is already playing the role.
         */
        PLAYING_ROLE,
        /**
         * Exception message when the role contains associated norms.
         */
        ROLE_CONTAINS_NORMS,
        /**
         * Exception message when the role is already registered in the unit.
         */
        ROLE_EXISTS_IN_UNIT,
        /**
         * Exception message when the role is played by some agents.
         */
        ROLE_IN_USE,
        /**
         * Exception message when the role not exists in the unit.
         */
        ROLE_NOT_EXISTS,
        /**
         * Exception message when the TargetAgentName is the same than
         * AgentName.
         */
        SAME_AGENT_NAME,
        /**
         * Exception message when the parent unit is the same than unit.
         */
        SAME_UNIT,
        /**
         * Exception message when the are subunits inside the unit.
         */
        SUBUNITS_IN_UNIT,
        /**
         * Exception message when the unit is already registered.
         */
        UNIT_EXISTS,
        /**
         * Exception message when the unit does not exist.
         */
        UNIT_NOT_EXISTS,
        /**
         * Exception message when trying to change the parent unit.
         */
        VIRTUAL_PARENT,
        /**
         * Exception message when the selected unit is the virtual unit.
         */
        VIRTUAL_UNIT,
        /**
         * Exception message when the visibility of the role is private and the
         * agent does not play any role in the unit.
         */
        VISIBILITY_ROLE
    };

    // -----------------------------------------------------------------
    // FIELDS of the class
    // -----------------------------------------------------------------

    /**
     * Bundle with the resources to use.
     */
    protected ResourceBundle bundle;

    // -----------------------------------------------------------------
    // CONSTRUCTORS of the class
    // -----------------------------------------------------------------

    /**
     * Create a new message container using the current locale.
     */
    public THOMASMessages() {
        this(Locale.getDefault());
    } // End constructor 'THOMASMessages()'

    /**
     * Create a new message container using the specified locale.
     * 
     * @param locale
     *            localization of the messages.
     * 
     * @throws MissingResourceException
     *             If could not found the resources for the specified locale.
     */
    public THOMASMessages(Locale locale) {
        String baseName = this.getClass().getName();
        this.bundle = ResourceBundle.getBundle(baseName, locale);
    } // End constructor 'THOMASMessages(Locale)'

    // -----------------------------------------------------------------
    // METHODS of the class
    // -----------------------------------------------------------------

    /**
     * Return the string representation of the specified message.
     * 
     * @param id
     *            of the message to obtain.
     * 
     * @return the localized message string, or an empty string if it is not
     *         defined.
     */
    public String getString(MessageID id) {

        if (id != null) {

            return this.bundle.getString(id.toString());

        } else {

            return "";
        }

    }// End method 'getString(MessageID)'

    /**
     * Format the message identified by the specified identifier.
     * 
     * @param id
     *            of the message to obtain.
     * @param args
     *            arguments to replace in the message.
     * 
     * @return the localized message, or an empty string if it is not defined.
     * 
     * @see MessageFormat#format(String, Object...)
     */
    public String getMessage(MessageID id, Object... args) {

        if (id != null) {

            return MessageFormat.format(this.bundle.getString(id.toString()), args);

        } else {

            return "";
        }

    }// End method 'getMessage(MessageID,Object...)'

}
