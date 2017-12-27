package com.fastdfs;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException, MyException {

		demo demo = new demo();
		/**
		 * 上传文件
		 */
		// for(int a=0;a<1000;a++){
		// demo.upload();
		// }
		 demo.upload();

		/**
		 * 查看文件信息
		 */
		// demo.sellinfo();

		/**
		 * 删除文件
		 */
		// demo.delete();

		/**
		 * 下载文件
		 */
		// demo.download();
		
		/**
		 * 生成token
		 */
//		 try {
//		 demo.Token();
//		 } catch (NoSuchAlgorithmException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }

	}
}
