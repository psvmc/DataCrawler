package com.controller.ht;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.core.Controller;
import com.utils.ZJ_FileUtils;
import com.utils.ZJ_StringUtils;
import com.vo.ArticleVo;

public class TArticleController extends Controller {
	String fileBasePath;
	String cacId;

	private ArticleVo getDetail(ArticleVo articleVo) {
		Document doc;

		try {
			System.out.println("抓取的详情页URL:"+articleVo.getUrl());
			doc = Jsoup.connect(articleVo.getUrl()).get();
			Elements titleEle = doc.getElementsByAttributeValue("class",
					"title7");
			Elements contentEle = doc
					.getElementsByAttributeValue("class", "co");

			String sourceStr = doc.getElementsContainingText("来源").last()
					.html();
			sourceStr = sourceStr.replace("来源：", "")
					.replace("&nbsp;&nbsp;", "");

			String addDateStr = articleVo.getAddDate();
			String addDateStr2 = addDateStr.replace("-", "");
			String filePath001 = "webpic/W0" + addDateStr2.substring(0, 6)
					+ "/W" + addDateStr2 + "/W";
			Elements imageEles = contentEle.get(0).getElementsByTag("img");
			for (Element imageEle : imageEles) {
				String imageUrl = doc.baseUri() + imageEle.attr("src");
				String fileName = ZJ_StringUtils.getFileName(imageUrl);
				String filePath = fileBasePath + filePath001 + fileName;
				downloadFile(imageUrl, filePath);
				String src = filePath001 + fileName;
				String oldSrc = ZJ_StringUtils.getFileName(src);
				imageEle.attr("src", "/" + src);
				imageEle.attr("oldsrc", oldSrc);
				System.out.println(imageUrl);
				System.out.println(filePath);

			}
			articleVo.setSource(sourceStr);
			articleVo.setTitle(titleEle.text());
			String contentEleHtml = contentEle.html()
					.replaceAll("(\r\n|\r|\n|\n\r)", "<br />")
					.replaceAll("\'", "\"");
			String contentEleText = contentEle.text()
					.replaceAll("(\r\n|\r|\n|\n\r)", "<br />")
					.replaceAll("\'", "\"");
			if (contentEleHtml.getBytes("UTF-8").length >= 4000) {
				if (contentEleText.getBytes("UTF-8").length >= 4000) {
					articleVo.setContent(subStringByByte(contentEleText, 4000));
					articleVo.setContentText(subStringByByte(contentEleText, 4000));
				} else {
					articleVo.setContent(contentEleText);
					articleVo.setContentText(contentEleText);
				}

			} else {
				articleVo.setContent(contentEleHtml);
				articleVo.setContentText(contentEleText);
			}

		} catch (Exception e) {
			System.out.println("页面为空");
		}
		return articleVo;
	}

	public String subStringByByte(String str, int bypeLength) {
		boolean isOk = false;
		try {
			if (str.getBytes("UTF-8").length <= bypeLength) {
				return str;
			} else {
				while (!isOk) {
					str = removeString(str, bypeLength);
					if(str.getBytes("UTF-8").length<=4000){
						isOk = true;
					}
				}
				return str;
			}
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	private String removeString(String str, int bypeLength) {
		if(null == str){
			return str;
		}
		try {
			int strByteLength = str.getBytes("UTF-8").length;
			if (strByteLength - bypeLength >= 3000) {
				str = str.substring(0, str.length() - 1000);
				return str;
			} else if (strByteLength - bypeLength >= 1200) {
				str = str.substring(0, str.length() - 400);
				return str;
			} else if (strByteLength - bypeLength >= 600) {
				str = str.substring(0, str.length() - 200);
				return str;
			} else if (strByteLength - bypeLength >= 300) {
				str = str.substring(0, str.length() - 100);
				return str;
			} else if (strByteLength - bypeLength >= 150) {
				str = str.substring(0, str.length() - 50);
				return str;
			} else if (strByteLength - bypeLength >= 30) {
				str = str.substring(0, str.length() - 10);
				return str;
			} else if (strByteLength - bypeLength >= 15) {
				str = str.substring(0, str.length() - 5);
				return str;
			} else if (strByteLength - bypeLength >= 6) {
				str = str.substring(0, str.length() - 2);
				return str;
			} else if(strByteLength - bypeLength >= 3){
				str = str.substring(0, str.length() - 1);
				return str;
			}else{
				str = str.substring(0, str.length() - 1);
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}

	public void downloadFile(String remoteFilePath, String localFilePath) {
		URL urlfile = null;
		HttpURLConnection httpUrl = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		File f = new File(localFilePath);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			urlfile = new URL(remoteFilePath);
			httpUrl = (HttpURLConnection) urlfile.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(f));
			int len = 2048;
			byte[] b = new byte[len];
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			bos.flush();
			bis.close();
			httpUrl.disconnect();
		} catch (Exception e) {
			System.out.println("图片不存在：" + remoteFilePath);
		} finally {
			try {

				if (null != bis) {
					bis.close();
				}
				if (null != bos) {
					bos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private List<ArticleVo> getArticleList(int offset) {
		System.out.println("当前抓取的offset为：" + offset);
		System.out.println("当前抓取的cacId为：" + cacId);
		List<ArticleVo> arts = new ArrayList<ArticleVo>();
		Document doc;

		try {
			String weburl = "http://www.guancheng.gov.cn/viewCmsCac.do?offset="+ offset+"&cacId="+cacId;
			System.out.println("请求的URL为："+weburl);
			doc = Jsoup.connect(weburl).get();
			String baseUrl = doc.baseUri();
			Elements listOuterEle = doc.getElementsByAttributeValue("class",
					"xin2zuo");
			Elements trEles = listOuterEle.get(0).getElementsByTag("tr");
			for (int i = trEles.size() - 1; i >= 0; i--) {
				Element ele = trEles.get(i);
				Element trEle = ele.getAllElements().get(0);
				Elements tdEles = trEle.getElementsByTag("td");
				if (tdEles.size() == 3) {
					ArticleVo articleVo = new ArticleVo();
					Element titleA = trEle.getElementsByTag("td").get(1);
					String title = titleA.text();
					String url = titleA.getElementsByTag("a").attr("href");
					url = baseUrl + url;
					String addDate = trEle.getElementsByTag("td").get(2).html();
					addDate = addDate.replace("[", "").replace("]", "");
					articleVo.setTitle(title);
					articleVo.setAddDate(addDate);
					articleVo.setUrl(url);
					articleVo = getDetail(articleVo);
					if (!articleVo.getContent().equals("")) {
						arts.add(articleVo);
					}

				}
			}

		} catch (IOException e) {
			System.out.println("网址无法访问");
		}
		return arts;
	}

	private String getSql(List<ArticleVo> artList) {
		List<String> sqlList = new ArrayList<String>();
		for (ArticleVo articleVo : artList) {

			String sql = String
					.format("insert into WCMDOCUMENT "
							+ "(DOCID,DOCCHANNEL,DOCTYPE,DOCTITLE,DOCSECURITY,DOCSTATUS,DOCKIND,DOCCONTENT,"
							+ "DOCHTMLCON,DOCEDITOR,DOCOUTUPID,DOCRELTIME,CRUSER,CRTIME,DOCWORDSCOUNT,DOCPUBHTMLCON,"
							+ "ATTACHPIC,OPERTIME,OPERUSER,DOCSOURCENAME,SITEID,SRCSITEID,DOCFORM,DOCLEVEL)"
							+ " VALUES"
							+ " (%s,%s,%s,'%s',%s,%s,%s,'%s',"
							+ "'%s','%s',%s,to_date('%s','yyyy-MM-dd'),'%s',%s,%s,'%s',"
							+ "%s,%s,'%s','%s',%s,%s,%s,%s)",
							"(select max(DOCID)+1 from WCMDOCUMENT)", 100, 20,
							articleVo.getTitle(), 0, 1, 0,
							articleVo.getContentText(), articleVo.getContent(),
							"admin", 0, articleVo.getAddDate(), "admin",
							"sysdate", articleVo.getContentText().length(),
							articleVo.getContent(), 0, "sysdate", "admin",
							articleVo.getSource(), 4, 4, 1, 1);

			String sql2 = String
					.format("insert into WCMCHNLDOC "
							+ "(CHNLID,DOCID,DOCSTATUS,CRUSER,CRTIME,DOCRELTIME,SITEID,SRCSITEID,DOCFORM,DOCLEVEL,ATTACHPIC,POSCHNLID,RECID,DOCCHANNEL,OPERUSER,OPERTIME)"
							+ " VALUES"
							+ " (%s,%s,%s,'%s',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'%s',%s)",
							100, "(select max(DOCID) from WCMDOCUMENT)", 1,
							"admin", "sysdate", "sysdate", 4, 4, 1, 1, 0, 100,
							"(select max(RECID)+1 from WCMCHNLDOC)", 100,
							"admin", "sysdate");

			sqlList.add(sql);
			sqlList.add(sql2);
		}
		return ZJ_StringUtils.listToStr(sqlList, ";\n");

	}

	public void list() {
		int offset=getParaToInt("offset");
		this.cacId = getPara("cacId");
		fileBasePath = ZJ_FileUtils.getBaseFilePath(getRequest());
		List<ArticleVo> arts = getArticleList(offset);
		String sqls = getSql(arts);
		sqls += ";\ncommit;";
		sqls = "Set define off;\n"+sqls;
		renderText(sqls);
	}

}
