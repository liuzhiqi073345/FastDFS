package com.example.demo.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.com.fastdfs.demo;
import com.example.demo.org.csource.common.MyException;

@RestController
public class TestController {

	@Autowired
	FileRepository fileRepository;

	/*
	 * 选取文件上传
	 */
	@PostMapping("/updateurl")
	public void updateurl(HttpServletRequest request) throws IOException, MyException {
		demo demo = new demo();
		List<String[]> list = demo.updateurl(request);
		for (int i = 0; i < list.size(); i++) {

			String str[] = list.get(i);
			FastDfs fastDfs = new FastDfs();
			fastDfs.setGroupname(str[0]);
			fastDfs.setFilename(str[1]);

			fileRepository.save(fastDfs);
		}

	}

	/*
	 * 默认的c盘下1.jpg 2.jpg 3.jpg三个文件的上传
	 */
	@PostMapping("/addAll")
	public String addAll() throws IOException, MyException {

		demo demo = new demo();

		for (int i = 1; i <= 3; i++) {
			String str[] = demo.upload(String.valueOf(i));

			FastDfs fastDfs = new FastDfs();
			fastDfs.setGroupname(str[0]);
			fastDfs.setFilename(str[1]);

			fileRepository.save(fastDfs);
		}

		return "Add success!";
	}

	/*
	 * 删除所有数据库中保存了的文件
	 */
	@DeleteMapping("/deleteAll")
	public String deleteAll() throws IOException, MyException {
		List<FastDfs> list = fileRepository.findAll();
		fileRepository.deleteAll();
		demo demo = new demo();
		for (int i = 0; i < list.size(); i++) {
			demo.delete(list.get(i).getGroupname(), list.get(i).getFilename());
		}
		return "Delete success!";
	}

	/*
	 * 获取文件（图片），在前台进行显示
	 */
	@GetMapping("/getImage")
	public List<StringBuffer> getImage() throws NoSuchAlgorithmException, IOException, MyException {
		demo demo = new demo();
		List<FastDfs> fastDfsList = new ArrayList<>();
		fastDfsList = fileRepository.findAll();

		List<StringBuffer> list = new ArrayList<>();
		// 循环查询处理
		for (int i = 0; i < fastDfsList.size(); i++) {
			StringBuffer url = demo.Token(fastDfsList.get(i).getGroupname(), fastDfsList.get(i).getFilename());
			if (url != null || !"".equals(url)) {
				list.add(url);
			}
		}
		return list;
	}

	/*
	 * 下载到D盘下的imges文件夹中
	 */
	@GetMapping("/downloadAll")
	public String downloadAll() throws IOException, MyException {

		List<FastDfs> list = fileRepository.findAll();
		if (null != list && list.size() > 0) {
			demo demo = new demo();
			for (int i = 0; i < list.size(); i++) {
				demo.download(list.get(i).getGroupname(), list.get(i).getFilename());
			}
			return "Download success!";
		} else {
			return "没有图片资源以供下载！";
		}
	}

	/*
	 * 直接访问文件服务器压缩成zip文件进行下载
	 */
	@GetMapping("/downloadRemote")
	public void downloadRemote(HttpServletRequest request, HttpServletResponse response)
			throws NoSuchAlgorithmException, IOException, MyException {

		// 查询数据库连接服务器获取文件url
		demo demo = new demo();
		List<FastDfs> fastDfsList = new ArrayList<>();
		fastDfsList = fileRepository.findAll();
		String files[] = new String[fastDfsList.size()];
		// 循环查询处理
		for (int i = 0; i < fastDfsList.size(); i++) {
			StringBuffer url = demo.Token(fastDfsList.get(i).getGroupname(), fastDfsList.get(i).getFilename());
			if (url != null || !"".equals(url)) {
				files[i] = url.toString();
			}
		}

		// 压缩文件
		String downloadFilename = "images.zip";// 文件的名称
		downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");// 转换中文否则可能会产生乱码
		response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
		response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		for (int i = 0; i < files.length; i++) {
			URL url = new URL(files[i]);
			zos.putNextEntry(new ZipEntry(i + ".jpg"));
			// FileInputStream fis = new FileInputStream(new File(files[i]));
			InputStream fis = url.openConnection().getInputStream();
			byte[] buffer = new byte[1024];
			int r = 0;
			while ((r = fis.read(buffer)) != -1) {
				zos.write(buffer, 0, r);
			}
			fis.close();
		}
		zos.flush();
		zos.close();
	}

}
