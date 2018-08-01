package Http;

public class UploadedFile {

    private String fileName;
    private String extension;
    private String content = "";
    private String path;
    private String fullFileName;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }
}
