package com.model;

import org.apache.log4j.Logger;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class TArticle extends Model<TArticle> {
	Logger logger = Logger.getLogger(Object.class);
	public static final TArticle dao = new TArticle();

}