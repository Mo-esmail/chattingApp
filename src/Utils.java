
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    public static final String HAND_SHAKE = "S";
    public static final String USER_NAME_USED = "USED";
    public static final String END_CONNECTION = "E";
    public static final String MESSAGE = "M";
    public static final String BROADCAST = "B";
    public static final String UPDATE_USERS = "U";
    public static final String END_LINE_TOKEN = "END";
    public static final String SEPERATOR = ":";

    public static String prepareUserNameProblem() {
        return USER_NAME_USED + SEPERATOR + "User name is used" + SEPERATOR + END_LINE_TOKEN;
    }
    /**
     * @param userName
     *            the user initializes the connection
     *
     * @return S:UserName:END
     */
    public static String prepareHandShake(String userName) {
        return HAND_SHAKE + SEPERATOR + userName + SEPERATOR + END_LINE_TOKEN;
    }

    public static String processHandShake(String message) {
        message = eleminateStartEnd(message, HAND_SHAKE);
        return message;
    }
    /**
     * @param userName
     *            the user send the message
     * @param message
     *            the message to be sent to all members
     * @return: M:UserName:message:END
     */
    public static String prepareBroadcast(String userName,String message) {
        return BROADCAST + SEPERATOR + userName + SEPERATOR + message + SEPERATOR + END_LINE_TOKEN;
    }
    /**
     *
     * @param message
     * @return array of String
     * 0 : sender
     * 1: message
     */
    public static String[] processBroadcast(String message) {
        message = eleminateStartEnd(message, BROADCAST);
        return message.split(SEPERATOR);
    }

    /**
     * @param userName
     *            the user send the message
     * @param receiverUser
     *            the user to receive the message
     * @param message
     *            the message to be sent to the server
     * @return: M:UserName:receiverUser:message:END
     */

    public static String prepareMessage(String userName, String receiverUser,
                                        String message) {
        return MESSAGE + SEPERATOR + userName + SEPERATOR + receiverUser
                + SEPERATOR + message + SEPERATOR + END_LINE_TOKEN;
    }
    /**
     *
     * @param message
     * @return array of String
     * 0 : sender
     * 1: receiver
     * 2: message
     */
    public static String[] processMessage(String message) {
        message = eleminateStartEnd(message, MESSAGE);
        return message.split(SEPERATOR);
    }

    /**
     * @return U:activeUser1:activeUser2.....:END
     */
    public static String prepareUsersUpdate(ArrayList<String> activeUsers) {
        StringBuilder messageBuilder = new StringBuilder(UPDATE_USERS
                + SEPERATOR);
        for (String user : activeUsers)
            messageBuilder.append(user + SEPERATOR);
        messageBuilder.append(END_LINE_TOKEN);
        return messageBuilder.toString();
    }

    public static String[] processUsersUpdate(String message) {
        message = eleminateStartEnd(message, UPDATE_USERS);
        return message.split(SEPERATOR);
    }
    /**
     *
     * @param userName
     * @return E:username:END
     */
    public static String prepareEndConnection(String userName) {
        return END_CONNECTION + SEPERATOR + userName  + SEPERATOR + END_LINE_TOKEN;
    }
    /**
     *
     * @param message
     * @return array of String
     * 0 : sender
     */
    public static String[] processEndConnection(String message) {
        message = eleminateStartEnd(message, END_CONNECTION);
        return message.split(SEPERATOR);
    }
    /**
     *
     * @param message
     * @return
     * 	0: handSake
     * 	1:Message
     * 	2:Broadcast
     * 	3:updateUsers
     * 	4:End Connection
     *  5:User name is used
     * -1: unidentified
     */
    public static int identifyMessage(String message)
    {
        String[] split=message.split(SEPERATOR);
        if(split[0].equals(HAND_SHAKE))
            return 0;
        if(split[0].equals(MESSAGE))
            return 1;
        if(split[0].equals(BROADCAST))
            return 2;
        if(split[0].equals(UPDATE_USERS))
            return 3;
        if(split[0].equals(END_CONNECTION))
            return 4;
        if(split[0].equals(USER_NAME_USED))
            return 5;
        return -1;
    }
    public static String eleminateStartEnd(String message, String messageType) {
        message = message.replaceFirst(messageType + SEPERATOR, "");
        message = message.substring(0,message.lastIndexOf(SEPERATOR + END_LINE_TOKEN));
        return message;
    }
    private static SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
    public static String getCurrentDate()
    {
        return formatter.format(new Date());
    }
}
