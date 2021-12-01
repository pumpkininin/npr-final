package data;

public class FileInfo {
    private long fileSize;
    private String fileName;
    private byte[] data;

    public FileInfo() {
    }

    public FileInfo(long fileSize, String fileName, byte[] data) {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.data = data;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}