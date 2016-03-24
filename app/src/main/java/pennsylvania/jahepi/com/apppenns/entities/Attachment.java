package pennsylvania.jahepi.com.apppenns.entities;

import java.io.Serializable;

/**
 * Created by jahepi on 23/03/16.
 */
public class Attachment implements Serializable {

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
            if (mime != null) {
                return mime;
            }
            return "";
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
