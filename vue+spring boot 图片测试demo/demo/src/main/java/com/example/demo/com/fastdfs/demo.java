package com.example.demo.com.fastdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.demo.org.csource.common.MyException;
import com.example.demo.org.csource.common.NameValuePair;
import com.example.demo.org.csource.fastdfs.ClientGlobal;
import com.example.demo.org.csource.fastdfs.FileInfo;
import com.example.demo.org.csource.fastdfs.ProtoCommon;
import com.example.demo.org.csource.fastdfs.StorageClient;
import com.example.demo.org.csource.fastdfs.StorageClient1;
import com.example.demo.org.csource.fastdfs.StorageServer;
import com.example.demo.org.csource.fastdfs.TrackerClient;
import com.example.demo.org.csource.fastdfs.TrackerServer;

/**
 * FastDFS demo
 * 
 */
public class demo {

	/*
	 * 上传文件（选取文件上传）
	 */
	public List<String[]> updateurl(HttpServletRequest request) throws IOException, MyException {
		List<String[]> list = new ArrayList<>();
		if (request instanceof MultipartHttpServletRequest) { // 如果你现在是MultipartHttpServletRequest对象
			MultipartHttpServletRequest mrequest = (MultipartHttpServletRequest) request;
			Iterator<String> name = mrequest.getFileNames();
			while (name.hasNext()) {
				String fileName = name.next();
				MultipartFile file = mrequest.getFile(fileName);
				if (file != null) {
					// 上传文件
					String fileExtName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
					ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
					ClientGlobal.init(classPathResource.getClassLoader().getResource("fdfs_client.conf").getPath());
					TrackerClient trackerClient = new TrackerClient();
					TrackerServer trackerServer = trackerClient.getConnection();
					StorageServer storageServer = null;
					StorageClient1 storageClient = new StorageClient1(trackerServer, storageServer);
					NameValuePair[] metaList = new NameValuePair[3];
					metaList[0] = new NameValuePair("fileName", file.getOriginalFilename());
					metaList[1] = new NameValuePair("fileExtName", fileExtName);
					metaList[2] = new NameValuePair("fileLength", String.valueOf(file.getSize()));
					String upload_file[] = storageClient.upload_file(file.getBytes(), fileExtName, metaList);
					trackerServer.close();
					list.add(upload_file);
				}
			}
		}
		return list;
	}

	/*
	 * 上传文件（此方法设置的是本地的图片，比较死板）
	 */
	public String[] upload(String name) throws IOException, MyException {
		// 1、设置一个上传文件的路径；
		File imgFile = new File("C:" + File.separator + name + ".jpg");
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
		trackerServer.close();
		return fileId;
	}

	/*
	 * 查看文件信息
	 */
	public void sellinfo(String name) throws IOException, MyException {
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
		// String fileName =
		// "group1/M00/00/00/wKgRglo42reAfmHiAADwQmFmfec069.jpg";
		String fileName = name;
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
	public int delete(String group, String name) throws IOException, MyException {
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
		String fileName = group + "/" + name;
		// 7、删除文件，返回值为0则删除成功
		// System.out.println("删除成功？0-成功、非0-不成功：" +
		// client.delete_file1(fileName));
		int isok = client.delete_file1(fileName);
		trackerServer.close();
		return isok;
	}

	/*
	 * 下载文件
	 */
	public void download(String group, String name) throws IOException, MyException {

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
		byte[] b = client.download_file(group, name);
		// 7、创建本地文件路径
		String filePath = "D:/images";
		File f = new File(filePath);
		if (!f.exists()) {
			f.mkdir();
		}
		// 8、保存文件
		IOUtils.write(b, new FileOutputStream(filePath + "/" + UUID.randomUUID().toString() + ".jpg"));
		System.out.println("下载文件成功：" + b);
	}

	/*
	 * 查看文件，通过生成token的方式查看
	 */
	public StringBuffer Token(String group, String name) throws IOException, MyException, NoSuchAlgorithmException {
		// 1、读取上传的配置文件，此配置文件在src路径下
		ClassPathResource res = new ClassPathResource("fdfs_client.conf");
		// 2、初始化FastDFS上传的环境
		ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
		// 3、建立Tracker的客户端连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
		// 定义文件id的时候千万不要加上组名，否则无法访问
		// String fileId = "M00/00/08/wKgRiFo8mO-AaIg6AADwQmFmfec048_big.jpg";
		String fileId = name;
		int ts = (int) (System.currentTimeMillis() / 1000); // 取得当前的一个时间标志，服务器时间必须和客户端机器时间保持一致
		String token = ProtoCommon.getToken(fileId, ts, ClientGlobal.getG_secret_key());
		StringBuffer fileUrl = new StringBuffer();
		fileUrl.append("http://");
		fileUrl.append(trackerServer.getInetSocketAddress().getHostString());
		fileUrl.append("/" + group + "/").append(fileId);
		fileUrl.append("?token=").append(token).append("&ts=").append(ts);
		trackerServer.close();
		return fileUrl;
	}
}
