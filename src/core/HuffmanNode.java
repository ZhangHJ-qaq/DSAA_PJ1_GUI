package core;


import java.io.Serializable;

/**
 * 哈夫曼树中的节点
 */
public class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
    byte data;//哈夫曼节点中的byte数据（1字节）
    int weight;//节点的权重
    HuffmanNode left;//左边的节点
    HuffmanNode right;//右边的节点


    /**
     * 根据权重比较哈夫曼节点的先后顺序，小的在前
     *
     * @param o 哈夫曼节点
     * @return
     */
    @Override
    public int compareTo(HuffmanNode o) {
        return this.weight - o.weight;
    }

    public HuffmanNode(byte data, int weight, HuffmanNode left, HuffmanNode right) {
        this.data = data;
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    public HuffmanNode() {
    }

    /**
     * 判断该节点是不是叶子节点
     *
     * @return 如果是，返回true。如果不是，返回false
     */
    public boolean isLeafNode() {
        if (this.left == null && this.right == null) {
            return true;
        }
        return false;
    }
}
