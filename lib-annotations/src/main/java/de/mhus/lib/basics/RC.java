package de.mhus.lib.basics;

public class RC {

    public enum CAUSE {
        ENCAPSULATE, // encapsulate cause state
        ADAPT, // if possible adapt cause state
        APPEND, // if possible adapt cause state and append message entries
        IGNORE, // do print as parameters
        HIDE // print as parameters but do not link as cause
    }
    
    public enum STATUS {
        
        WARNING_TEMPORARILY(199),
        OK(200),
        CREATED(201),
        ACCEPTED(202),
        WARNING(299),
        ERROR(400),
        ACCESS_DENIED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        CONFLICT(409),
        GONE(410),
        TOO_LARGE(413),
        SYNTAX_ERROR(415),
        TEAPOT(418),
        USAGE(422),
        LIMIT(427),
        INTERNAL_ERROR(500),
        NOT_SUPPORTED(501),
        BUSY(503),
        TIMEOUT(504),
        TOO_DEEP(508)
        ;
        
        private final int rc;
        
        private STATUS(int rc) {
            this.rc = rc;
        }
        
        public int rc() {
            return rc;
        }
        
    }
    
    /**
     * Miscellaneous warning
     */
    public static final int WARNING_TEMPORARILY = 199; // Miscellaneous warning
    
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    /**
     * Miscellaneous persistent warning
     */
    public static final int WARNING = 299; // Miscellaneous persistent warning, https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46

    // do not retry with these errors - professional errors
    /**
     * Default Error, client error
     */
    public static final int ERROR = 400;
    /**
     * you are not allowed to access the system,
     * causes the client system to ask for a password,
     * exception for authentication
     */
    public static final int ACCESS_DENIED = 401;
    /**
     * you are not allowed to access this resource,
     * user and password is given but access ins not granted,
     * exception for authorization
     */
    public static final int FORBIDDEN = 403;
    /**
     * Resource was not found and thats ok 
     * - in contrast to CONFLICT - resource should be there
     */
    public static final int NOT_FOUND = 404;
    /**
     * conflict state or wrong state, to many retries,
     * key to remove not found, key already set
     * wrong configuration - if not fixed fast,
     * result is null,
     * duplicate entry,
     * device is not enabled
     * ... is not writable / read only
     */
    public static final int CONFLICT = 409; // conflict state or wrong state
    /**
     * indicating that the resource requested by the client has been permanently deleted, 
     * and that the client should not expect an alternative redirection or forwarding address
     */
    public static final int GONE = 410;
    public static final int TOO_LARGE = 413;
    /**
     * Unsupported Media Type - string instead of int,
     * wrong parameter value type,
     * malformed format
     */
    public static final int SYNTAX_ERROR = 415; // Unsupported Media Type - string instead of int
    /**
     * Unprocessable Entity - parameter from client not set,
     * parameter not found, parameter data is null
     */
    public static final int USAGE = 422; // Unprocessable Entity - parameter not set

    /**
     * Too Many Requests, Limit exceeded
     */
    public static final int LIMIT = 427; // Too Many Requests
    
    public static final int TEAPOT = 418; // I’m a teapot - joke

    // retry later - technical errors
    /**
     * Internal Server Error, general server error
     */
    public static final int INTERNAL_ERROR = 500; // Internal Server Error
    /**
     * Not Implemented, method or operation not found
     */
    public static final int NOT_SUPPORTED = 501; // Not Implemented
    /**
     * Service Unavailable, do not use 403 because it could be repeated,
     * Resource currently not available, locked
     */
    public static final int BUSY = 503; // Service Unavailable, do not use 403 because it could be repeated
    /**
     * Gateway Timeout
     */
    public static final int TIMEOUT = 504; // Gateway Timeout
    /**
     * Loop Detected, to deep iteration, stack overflow
     */
    public static final int TOO_DEEP = 508; // Loop Detected

    public static final int RANGE_MIN_SUCCESSFUL = 200;
    public static final int RANGE_MAX_SUCCESSFUL = 299;
    public static final int RANGE_MIN_PROFESSIONAL = 400; // do not retry
    public static final int RANGE_MAX_PROFESSIONAL = 499; // do not retry
    public static final int RANGE_MIN_TECHNICAL = 500; // do retry later
    public static final int RANGE_MAX_TECHNICAL = 599; // do retry later

    public static final int RANGE_MIN_CUSTOM = 900;
    public static final int RANGE_MAX_CUSTOM = 999;

    public static final int RANGE_MAX = 999;

    public static String toMessage(int rc, IResult cause, String msg, Object[] parameters, int maxSize) {
        return toMessage(rc, CAUSE.IGNORE, msg, parameters, maxSize, cause);
    }

    public static String toMessage(int rc, CAUSE causeHandling, String msg, Object[] parameters, int maxSize) {
        return toMessage(rc, null, msg, parameters, maxSize, null);
    }

    public static String toMessage(int rc, CAUSE causeHandling, String msg, Object[] parameters, int maxSize, IResult cause) {
        if (causeHandling == null) causeHandling = CAUSE.ENCAPSULATE;
        // short cuts
        if (msg == null && parameters == null) return "["+ (rc >= 0 ? rc : "") +"]";
        if (parameters == null && msg.indexOf('"') == -1) return "[" + (rc >= 0 ? rc + "," : "") + "\"" + msg + "\"]";
        // pipe to colon
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (rc >= 0)
            sb.append(rc).append(",");
        addEncoded(sb,msg);
        if (parameters != null && parameters.length > 0) {
            boolean firstException = true;
            IResult appendCause = null;
            for (Object parameter : parameters) {

                sb.append(",");

                if (maxSize > 0 && sb.length() > maxSize) {
                    sb.append(" ...");
                    break;
                }
                
                if (parameter !=  null) {
                    
                    if (parameter instanceof IResult && causeHandling == CAUSE.ADAPT) 
                        return ((IResult)parameter).getMessage();
                    if (parameter instanceof IResult && causeHandling == CAUSE.APPEND) {
                        appendCause = (IResult)parameter;
                        firstException = false; // ignore only first exception - it's the cause exception
                        sb.setLength(sb.length()-1);
                        continue;
                    }
                    if( parameter instanceof Throwable && causeHandling != CAUSE.IGNORE && firstException) {
                        firstException = false; // ignore only first exception - it's the cause exception
                        sb.setLength(sb.length()-1);
                        continue;
                    }
                    if (parameter instanceof Object[]) {
                        sb.append("[");
                        boolean first2 = true;
                        for (Object item : (Object[])parameter) {
                            if (first2) first2 = false; else sb.append(",");
                            if (item == null)
                                sb.append("null");
                            else {
                                addEncoded(sb,item);
                            }
                        }
                        sb.append("]");
                        continue;
                    } else {
                        addEncoded(sb,parameter);
                    }
                } else
                    sb.append("null");
            }
            if (appendCause != null) {
                String msg2 = appendCause.getMessage();
                if (msg2 != null) {
                    if (sb.length() > 0)
                        sb.append(",");
                    if (msg2.startsWith("[") && msg2.endsWith("]"))
                        sb.append(msg2);
                    else
                        addEncoded(sb, msg2);
                }
            }
            if (cause != null) {
                String msg2 = cause.getMessage();
                if (msg2 != null) {
                    if (sb.length() > 0)
                        sb.append(",");
                    if (msg2.startsWith("[") && msg2.endsWith("]"))
                        sb.append(msg2);
                    else
                        addEncoded(sb, msg2);
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static void addEncoded(StringBuilder sb, Object obj) {
        if (obj == null) {
            sb.append("null");
            return;
        }
        String msg = String.valueOf(obj);
        int pos = 0;
        int nextPos;
        sb.append("\"");
        while ((nextPos = msg.indexOf('"', pos)) != -1) {
            sb.append(msg.substring(pos, nextPos));
            sb.append("\\\"");
            pos = nextPos+1;
            if (pos >= msg.length()) break;
        }
        if (pos < msg.length())
            sb.append(msg.substring(pos));
        sb.append("\"");
    }

    public static Throwable findCause(CAUSE causeHandling, Object... in) {
        if (in == null || (causeHandling != null && causeHandling == CAUSE.HIDE)) return null;
        for (Object o : in) {
            if (o instanceof Throwable) {
                return (Throwable) o;
            }
        }
        return null;
    }

    public static int findReturnCode(CAUSE causeHandling, int rc, Object... in) {
        if (causeHandling == null || in == null || (causeHandling != CAUSE.ADAPT && causeHandling != CAUSE.APPEND))
            return rc;
        for (Object o : in) {
            if (o instanceof IResult) {
                return ((IResult) o).getReturnCode();
            }
        }
        return rc;
    }
    

    
    /**
     * Allow all between 0 - 999, otherwise 400 (ERROR)
     * 
     * @param rc
     * @return normalize error code
     */
    public int normalize(int rc) {
        if (rc < 0) return ERROR;
        if (rc >= 1000) return ERROR;
        return rc;
    }
    /**
     * is successful but a warning
     * @param rc
     * @return true if this kind of error
     */
    public static boolean isWarning(int rc) {
        return rc == 0 || rc == WARNING || rc == WARNING_TEMPORARILY;
    }
    
    /**
     * not permanent, could be fixed
     * @param rc
     * @return true if this kind of error
     */
    public static boolean isTechnicalError(int rc) {
        return rc >= RANGE_MIN_TECHNICAL && rc <= RANGE_MAX_TECHNICAL;
    }
    
    /**
     * permanent error, retry will not fix it
     * @param rc
     * @return true if this kind of error
     */
    public static boolean isProfessionalError(int rc) {
        return rc < 0 || rc >= RANGE_MIN_PROFESSIONAL && rc <= RANGE_MAX_PROFESSIONAL || rc > RANGE_MIN_PROFESSIONAL;
    }
    
    /**
     * not an error - should not be used for exceptions
     * @param rc
     * @return true if this kind of error
     */
    public static boolean isSuccessful(int rc) {
        return rc >= 0 && rc <= RANGE_MAX_SUCCESSFUL;
    }

    public static boolean canRetry(int rc) {
        return rc >= RANGE_MIN_TECHNICAL && rc <= RANGE_MAX_TECHNICAL && rc != NOT_SUPPORTED ;
    }

    public static String toString(int rc) {
        switch (rc) {
        case WARNING_TEMPORARILY: return "WARNING_TEMPORARILY";
        case OK: return "OK";
        case CREATED: return "CREATED";
        case ACCEPTED: return "ACCEPTED";
        case WARNING: return "WARNING";
        case ERROR: return "ERROR";
        case ACCESS_DENIED: return "ACCESS_DENIED";
        case FORBIDDEN: return "FORBIDDEN";
        case NOT_FOUND: return "NOT_FOUND";
        case GONE: return "GONE";
        case TOO_LARGE: return "TOO_LARGE";
        case SYNTAX_ERROR: return "SYNTAX_ERROR";
        case USAGE: return "USAGE";
        case TEAPOT: return "TEAPOT";
        case INTERNAL_ERROR: return "INTERNAL_ERROR";
        case NOT_SUPPORTED: return "NOT_SUPPORTED";
        case BUSY: return "BUSY";
        case TIMEOUT: return "TIMEOUT";
        case TOO_DEEP: return "TOO_DEEP";
        case CONFLICT: return "CONFLICT";
        case LIMIT: return "LIMIT";
        }
        return String.valueOf(rc);
    }

}