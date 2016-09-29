package com.controller.ht;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.JsonRender;
import com.jfinalExt.ZJ_Db;
import com.model.TArticle;
import com.utils.ZJ_DateUtils;
import com.utils.ZJ_FileUtils;
import com.utils.ZJ_StringUtils;
import com.vo.ArticleVo;
import com.vo.ResultVo;

public class TArticleController extends Controller {
	String fileBasePath;

	private ArticleVo getDetail(ArticleVo articleVo) {
		Document doc;

		try {
			doc = Jsoup.connect(articleVo.getUrl()).get();
			Elements titleEle = doc.getElementsByAttributeValue("class",
					"title7");
			Elements contentEle = doc
					.getElementsByAttributeValue("class", "co");

			String sourceStr = doc.getElementsContainingText("来源").last().html();
			sourceStr = sourceStr.replace("来源：", "")
					.replace("&nbsp;&nbsp;", "");
			
			String addDateStr = articleVo.getAddDate();
			String addDateStr2 = addDateStr.replace("-", "");
			String filePath001 = "webpic/W" + addDateStr2.substring(0, 6)
					+ "/W" + addDateStr2 + "/W";
			Elements imageEles = contentEle.get(0).getElementsByTag("img");
			for (Element imageEle : imageEles) {
				String imageUrl = doc.baseUri() + imageEle.attr("src");
				String fileName = ZJ_StringUtils.getFileName(imageUrl);
				String filePath = fileBasePath+filePath001 + fileName;
				downloadFile(imageUrl, filePath);
				String src = filePath001 + fileName;
				String oldSrc = ZJ_StringUtils.getFileName(src);
				imageEle.attr("src", "/"+src);
				imageEle.attr("oldsrc",oldSrc);
				System.out.println(imageUrl);
				System.out.println(filePath);

			}
			articleVo.setSource(sourceStr);
			articleVo.setTitle(titleEle.text());
			String contentEleHtml = contentEle.html().replaceAll("(\r\n|\r|\n|\n\r)", "<br />").replaceAll("\'", "\"");
			String contentEleText = contentEle.text().replaceAll("(\r\n|\r|\n|\n\r)", "<br />").replaceAll("\'", "\"");
			if(contentEleHtml.length()>=4000){
				if(contentEleText.length()>=4000){
					articleVo.setContent(contentEleText.substring(0,4000));
					articleVo.setContentText(contentEleText.substring(0,4000));
				}else{
					articleVo.setContent(contentEleText);
					articleVo.setContentText(contentEleText);
				}
				
			}else{
				articleVo.setContent(contentEleHtml);
				articleVo.setContentText(contentEleText);
			}
			

		} catch (Exception e) {
			System.out.println("页面为空");
		}
		return articleVo;
	}

	public void downloadFile(String remoteFilePath, String localFilePath) {
		URL urlfile = null;
		HttpURLConnection httpUrl = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		File f = new File(localFilePath);
		if(!f.exists()){
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
		}else{
			f.delete();
			try {
				f.createNewFile();
				} catch (IOException e) {
				// TODO Auto-generated catch block
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
			System.out.println("图片不存在："+remoteFilePath);
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
		List<ArticleVo> arts = new ArrayList<ArticleVo>();
		Document doc;

		try {
			doc = Jsoup
					.connect(
							"http://www.guancheng.gov.cn/viewCmsCac.do?cacId=ff80808132a8746f0132e1329a262e0d&offset="
									+ offset).get();
			String baseUrl = doc.baseUri();
			Elements listOuterEle = doc.getElementsByAttributeValue("class",
					"xin2zuo");
			Elements trEles = listOuterEle.get(0).getElementsByTag("tr");
			for (int i = trEles.size()-1; i >= 0; i--) {
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
			e.printStackTrace();
		}
		return arts;
	}

	private List<ArticleVo> getArticleListAll(int beginOffset, int endOffset) {
		List<ArticleVo> arts = new ArrayList<ArticleVo>();
		for (int i = beginOffset; i <= endOffset; i += 20) {
			List<ArticleVo> artsTemp = getArticleList(i);
			arts.addAll(artsTemp);
		}
		return arts;
	}
	
	private String getSql(List<ArticleVo> artList){
		List<String> sqlList = new ArrayList<String>();
		for (ArticleVo articleVo : artList) {

			String sql =String.format(
					"insert into WCMDOCUMENT "+
					"(DOCID,DOCCHANNEL,DOCTYPE,DOCTITLE,DOCSECURITY,DOCSTATUS,DOCKIND,DOCCONTENT," +
					"DOCHTMLCON,DOCEDITOR,DOCOUTUPID,DOCRELTIME,CRUSER,CRTIME,DOCWORDSCOUNT,DOCPUBHTMLCON," +
					"ATTACHPIC,OPERTIME,OPERUSER,DOCSOURCENAME,SITEID,SRCSITEID,DOCFORM,DOCLEVEL)"+
					" VALUES"+
					" (%s,%s,%s,'%s',%s,%s,%s,'%s',"+
					"'%s','%s',%s,to_date('%s','yyyy-MM-dd'),'%s',%s,%s,'%s',"+
					"%s,%s,'%s','%s',%s,%s,%s,%s)",
					"(select max(DOCID)+1 from WCMDOCUMENT)",100,20,articleVo.getTitle(),0,1,0,articleVo.getContentText(),
					articleVo.getContent(),"admin",0,articleVo.getAddDate(),"admin","sysdate",articleVo.getContentText().length(),articleVo.getContent(),
					0,"sysdate","admin",articleVo.getSource(),4,4,1,1);			
			
			String sql2 =String.format(
					"insert into WCMCHNLDOC "+
					"(CHNLID,DOCID,DOCSTATUS,CRUSER,CRTIME,DOCRELTIME,SITEID,SRCSITEID,DOCFORM,DOCLEVEL,ATTACHPIC,POSCHNLID,RECID,DOCCHANNEL,OPERUSER,OPERTIME)"+
					" VALUES"+
					" (%s,%s,%s,'%s',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'%s',%s)",
					100,"(select max(DOCID) from WCMDOCUMENT)",1,"admin","sysdate","sysdate",4,4,1,1,0,100,"(select max(RECID)+1 from WCMCHNLDOC)",100,"admin","sysdate");	
			
			sqlList.add(sql);
			sqlList.add(sql2);
		}
		return ZJ_StringUtils.listToStr(sqlList,";\n");

	}

	public void list() {
		fileBasePath = ZJ_FileUtils.getBaseFilePath(getRequest());
		List<ArticleVo> arts = getArticleListAll(5500, 5500);
		String sqls = getSql(arts);
		sqls+=";commit;";
		renderText(sqls);
	}

}
