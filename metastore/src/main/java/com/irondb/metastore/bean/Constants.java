package com.irondb.metastore.bean;

import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;

/**
 * @date 2015年10月11日
 * @author Administrator
 * @filename constants.java
 * @version
 * 
 */
class Constants {
	public static final Durability DURABILITY = Durability.SKIP_WAL;

	public static final DataBlockEncoding DATA_BLOCK_ENCODING = DataBlockEncoding.FAST_DIFF;

	public static final int MAX_VERSIONS = 1;

	public static final Algorithm ALGORITHM = Algorithm.SNAPPY;

	public static final int BLOCK_SIZE = 1024 * 1024;
}
