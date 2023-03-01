package oose.euphoria.backend.configuration.database;

public class Queries {
    public static final String SELECT_MESSAGE_BY_ID = "FROM Message WHERE id = :messageID";
    public static final String SELECT_UNREAD_MESSAGES_USERS = "SELECT DISTINCT senderID FROM Message WHERE receiverID = :receiverID";
    public static final String SELECT_UNREAD_MESSAGES = "FROM Message WHERE readDate IS NULL AND senderID = :senderID AND receiverID = :receiverID";
    public static final String SELECT_BROADCASTS = "FROM Message WHERE receiverID = :receiverID AND isBroadcast = 1";
    public static final String SELECT_MESSAGES_BETWEEN_USERS = "FROM Message WHERE isBroadcast = 0 AND ((senderID = :senderID AND receiverID = :receiverID) OR (senderID = :receiverID AND receiverID = :senderID)) AND (readDate > :oneDay OR readDate is NULL) ORDER BY sendDate";
    public static final String DELETE_MESSAGE_BY_TIME = "FROM Message WHERE (readDate <= :oneDay) OR ((sendDate <= :oneWeek) AND readDate IS NULL)";
    public static final String COUNT_UNREAD_MESSAGES = "SELECT COUNT(*) FROM Message WHERE readDate IS NULL AND senderID = :senderID AND receiverID = :receiverID";
    public static final String SELECT_USER = "FROM User WHERE id = :userID";
    public static final String SELECT_USERS_IN_COMPANY = "FROM User WHERE companyID = :companyID";
    public static final String SELECT_SPECIFIC_COMPANY = "FROM Company WHERE companyID = :companyID";
    public static final String SELECT_ATTACHMENT = "FROM Attachment WHERE id = :attachmentID";
    public static final String SELECT_ATTACHMENT_NAME = "SELECT name FROM Attachment WHERE id = :attachmentID";
    public static final String SELECT_USER_SUPPORT_COMPANY_IDS = "FROM CompanySupport WHERE userID = :userID";
    public static final String SELECT_SUPPORT_USERS = "SELECT userID FROM CompanySupport WHERE companyID = :companyID";
    public static final String USER_ID_PARAMETER = "userID";
    public static final String COMPANY_ID_PARAMETER = "companyID";
    private Queries() {
    }
}
