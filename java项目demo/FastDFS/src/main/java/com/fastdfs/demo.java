package com.fastdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

/**
 * FastDFS demo
 * 
 * @author WuWanfei
 *
 */
public class demo {

	/*
	 * 上传文件
	 */
	public void upload() throws IOException, MyException {
		// 1、设置一个上传文件的路径；
		File imgFile = new File("C:" + File.separator + "kaka.jpg");
		// 取得文件扩展名称
		String fileExtName = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
		// 2、读取上传的配置文件，此配置文件在src路径下
		ClassPathResource res = new ClassPathResource("fdfs_client.conf");
		// 3、初始化FastDFS上传的环境
		ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
		// 4、建立Tracker的客户端连接
		TrackerClient tracker = new TrackerClient();
		// 取得服务器连接
		TrackerServer trackerServer = tracker.getConnection();
		// 5、真正负责数据保存的是Storage
		StorageServer storageServer = null;
		// 6、进行StoragerClient的创建
		StorageClient client = new StorageClient(trackerServer, storageServer);
		// 7、创建文件的元数据对象
		NameValuePair[] metaList = new NameValuePair[3];
		metaList[0] = new NameValuePair("fileName", imgFile.getName());
		metaList[1] = new NameValuePair("fileExtName", fileExtName);
		metaList[2] = new NameValuePair("fileLength", String.valueOf(imgFile.length()));
		// 8、实现文件上传，返回文件的路径名称
		String[] fileId = client.upload_file(imgFile.getPath(), fileExtName, metaList);
		System.out.println(Arrays.toString(fileId));
		System.out.println(fileId[0]);
		trackerServer.close();
	}

	/*
	 * 查看文件信息
	 */
	public void sellinfo() throws IOException, MyException {
		// 1、读取上传的配置文件，此配置文件在src路径下
		ClassPathResource res = new ClassPathResource("fdfs_client.conf");
		// 2、初始化FastDFS上传的环境
		ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
		// 3、建立Tracker的客户端连接
		TrackerClient tracker = new TrackerClient();
		// 取得服务器连接
		TrackerServer trackerServer = tracker.getConnection();
		// 4、真正负责数据保存的是Storage
		StorageServer storageServer = null;
		// 5、进行StoragerClient的创建
		StorageClient1 client = new StorageClient1(trackerServer, storageServer);
		// 6、取得文件的相关信息
		String fileName = "group1/M00/00/00/wKgRglo42reAfmHiAADwQmFmfec069.jpg";
		// 取得该文件内容的信息
		FileInfo fi = client.get_file_info1(fileName);
		System.out.println("得到文件大小：" + fi.getFileSize());
		System.out.println("创建日期：" + fi.getCreateTimestamp());
		System.out.println("真实保存主机：" + fi.getSourceIpAddr());
		System.out.println("CRC32：" + fi.getCrc32());
		trackerServer.close();
	}

	/*
	 * 删除文件
	 */
	public void delete() throws IOException, MyException {
		// 1、读取上传的配置文件，此配置文件在src路径下
		ClassPathResource res = new ClassPathResource("fdfs_client.conf");
		// 2、初始化FastDFS上传的环境
		ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
		// 3、建立Tracker的客户端连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
		// 4、真正负责数据保存的是Storage
		StorageServer storageServer = null;
		// 5、进行StoragerClient的创建
		StorageClient1 client = new StorageClient1(trackerServer, storageServer);
		// 6、取得文件的相关信息
		String fileName = "group1/M00/00/00/wKgRglo42reAfmHiAADwQmFmfec069.jpg";
		// 7、删除文件，返回值为0则删除成功
		System.out.println("删除成功？0-成功、非0-不成功：" + client.delete_file1(fileName));
		trackerServer.close();
	}

	/*
	 * 下载文件
	 */
	public void download() throws IOException, MyException {

		// 1、读取上传的配置文件，此配置文件在src路径下
		ClassPathResource res = new ClassPathResource("fdfs_client.conf");
		// 2、初始化FastDFS上传的环境
		ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
		// 3、建立Tracker的客户端连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
		// 4、真正负责数据保存的是Storage
		StorageServer storageServer = null;
		// 5、进行StoragerClient的创建
		StorageClient1 client = new StorageClient1(trackerServer, storageServer);
		// 6、取得文件的字节
		byte[] b = client.download_file("group1", "M00/00/00/wKgRglo42reAfmHiAADwQmFmfec069.jpg");
		// 7、保存文件
		IOUtils.write(b, new FileOutputStream("D:/" + UUID.randomUUID().toString() + ".jpg"));
		System.out.println("下载文件成功：" + b);
	}

	/*
	 * 查看文件，通过生成token的方式查看
	 */
	public void Token() throws IOException, MyException, NoSuchAlgorithmException {
		// 1、读取上传的配置文件，此配置文件在src路径下
		ClassPathResource res = new ClassPathResource("fdfs_client.conf");
		// 2、初始化FastDFS上传的环境
		ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
		// 3、建立Tracker的客户端连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
		// 定义文件id的时候千万不要加上组名，否则无法访问
		String fileId = "M00/00/08/wKgRiFo8mO-AaIg6AADwQmFmfec048_big.jpg";
		int ts = (int) (System.currentTimeMillis() / 1000); // 取得当前的一个时间标志
		String token = ProtoCommon.getToken(fileId, ts, ClientGlobal.getG_secret_key());
		StringBuffer fileUrl = new StringBuffer();
		System.out.println();
		fileUrl.append("http://");
		fileUrl.append(trackerServer.getInetSocketAddress().getHostString());
		fileUrl.append("/group2/").append(fileId);
		fileUrl.append("?token=").append(token).append("&ts=").append(ts);
		System.out.println(fileUrl);
		trackerServer.close();
	}
}
