package smarter.uearn.money.chats.model;

public class SMEMessage {
    private String mMessage;
    private String mUsername;
    private String mTime;
    private String mType;
    private String mUserid;
    private String mMessageType;
    private String mUserColor;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getType() {
        return mType;
    }

    public String getUserid() {
        return mUserid;
    }

    public void setUserid(String mUserid) {
        this.mUserid = mUserid;
    }

    public String getMessageType() {
        return mMessageType;
    }

    public void setMessageType(String mMessageType) {
        this.mMessageType = mMessageType;
    }

    public String getUserColor() {
        return mUserColor;
    }

    public void setUserColor(String mUserColor) {
        this.mUserColor = mUserColor;
    }

    public static class Builder {
        private String mUsername;
        private String mMessage;
        private String mTime;
        private String mType;
        private String mUserid;
        private String mMessageType;
        private String mUserColor;


        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder time(String time) {
            mTime = time;
            return this;
        }

        public Builder type(String type) {
            mType = type;
            return this;
        }

        public Builder userid(String userid) {
            mUserid = userid;
            return this;
        }

        public Builder messageType(String messageType) {
            mMessageType = messageType;
            return this;
        }

        public Builder userColor(String userColor) {
            mUserColor = userColor;
            return this;
        }

        public SMEMessage build() {
            SMEMessage message = new SMEMessage();
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mTime = mTime;
            message.mType = mType;
            message.mUserid = mUserid;
            message.mMessageType = mMessageType;
            message.mUserColor = mUserColor;
            return message;
        }
    }
}
