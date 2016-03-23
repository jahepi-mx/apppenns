package pennsylvania.jahepi.com.apppenns.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Message extends Entity {

    private int id;
    private User from;
    private User to;
    private String message;
    private boolean delivered;
    private boolean read;
    private boolean readSync;
    private boolean send;
    private ArrayList<Attachment> attachments;

    public Message() {
        attachments = new ArrayList<Attachment>();
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public void addAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
    }

    public Iterator<Attachment> getAttachmentsIterator() {
        return attachments.iterator();
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getMessage() {
        if (message != null) {
            return message;
        }
        return "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isReadSync() {
        return readSync;
    }

    public void setReadSync(boolean readSync) {
        this.readSync = readSync;
    }

    @Override
    public boolean equals(Object o) {
        Message msg = (Message) o;
        if (msg.getTo().getId() == to.getId() && msg.getModifiedDateString().equals(getModifiedDateString())) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "to: " + to.getId() + " from: " + from.getId() + " date: " + getModifiedDateString();
    }

    public static class Attachment implements Serializable {

        private int id;
        private File file;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    public static class File extends Entity {

        private int id;
        private String name;
        private String path;
        private String mime;
        private boolean send;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public boolean isSend() {
            return send;
        }

        public void setSend(boolean send) {
            this.send = send;
        }

        public String getPathNoName() {
            return getPath().substring(0, getPath().lastIndexOf(java.io.File.separator));
        }
    }
}
