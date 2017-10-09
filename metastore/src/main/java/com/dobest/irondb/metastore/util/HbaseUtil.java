package com.dobest.irondb.metastore.util;

import com.irondb.metastore.IronDBContext;
import com.dobest.irondb.metastore.bean.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

/**
 * Created by Micheal on 2017/10/2.
 */
public class HbaseUtil {

    public static Configuration conf;
    private static Admin hAdmin;

    public static Connection connection;

    public void HbaseUtil(IronDBContext context) {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", context.get("hbase.zookeeper.quorum"));
        try {
            connection = ConnectionFactory.createConnection(conf);
            hAdmin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean dropTable(String tableName) {
        TableName tName = TableName.valueOf(tableName);
        try {
            if (hAdmin.tableExists(tName)) {
                hAdmin.disableTable(tName);
                hAdmin.deleteTable(tName);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createTable(HTableDescriptor httt, TableName tName) throws IOException {
        if (hAdmin.tableExists(tName)) {
            return false;
        }
        hAdmin.createTable(httt);
        return true;
    }

    public boolean createTable(HTableDescriptor htd, byte[][] splites, TableName tName) throws IOException {
        if (hAdmin.tableExists(tName)) {
            hAdmin.disableTable(tName);
            hAdmin.deleteTable(tName);
        }
        hAdmin.createTable(htd, splites);
        return true;
    }

    private byte[][] partitionAlgrothrim(int num, int rowKeyLength) {
        if (num <= 0) return null;
        byte[][] splitKeys = new byte[num][];
        int offset = getOffset(rowKeyLength);
        for (int i = 1; i < num; i++) {
//			splitKeys[i - 1] = new byte[] {  (byte) ((i << 6) & 0xffff) };
            byte[] bb = Bytes.toBytes(((i << offset) & 0xffff));
            splitKeys[i - 1] = Bytes.add(new byte[]{bb[2]}, new byte[]{bb[3]});
        }
        return splitKeys;
    }

    private int getOffset(int rowKeyLength) {
        int i = 0;
        while (rowKeyLength / 2 != 0) {
            i++;
            rowKeyLength = rowKeyLength / 2;
        }
        return i;
    }

    public boolean createHbaseTable(String table, List<String> Familys, int partitionNum, int rowkeyLength) {

        byte[][] splits = partitionAlgrothrim(partitionNum, rowkeyLength);
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(table));
        for (String columnFamily : Familys) {
            HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(columnFamily);
            hColumnDescriptor.setDataBlockEncoding(Constants.DATA_BLOCK_ENCODING);
            hColumnDescriptor.setMaxVersions(Constants.MAX_VERSIONS);
            hColumnDescriptor.setCompressionType(Constants.ALGORITHM);
            hColumnDescriptor.setBlocksize(Constants.BLOCK_SIZE);
            hColumnDescriptor.setBloomFilterType(BloomType.ROW);
            hTableDescriptor.addFamily(hColumnDescriptor);
        }
        try {
            hAdmin.createTable(hTableDescriptor, splits);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
