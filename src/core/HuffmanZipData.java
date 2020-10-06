package core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 哈夫曼压缩包中的数据
 */
public class HuffmanZipData implements Serializable {
    public static final String TYPE_SINGLE_FILE = "SINGLE_FILE";
    public static final String TYPE_DIRECTORY = "DIRECTORY";

    public String type;//记载了压缩的是单独的文件还是文件夹
    public long originalSize;//记载了压缩前的大小(Byte)
    public ArrayList<String> dirList;//记载了原先压缩文件夹的所有的子目录信息
    public ArrayList<HuffmanSingleFileData> fileList;//记载了压缩后各个文件的信息;

    public HuffmanZipData() {
        this.dirList = new ArrayList<>();
        this.fileList = new ArrayList<>();
    }
}
