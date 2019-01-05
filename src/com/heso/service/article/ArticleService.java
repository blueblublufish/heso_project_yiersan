package com.heso.service.article;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Flags.Flag;
import javax.print.attribute.Size2DSyntax;
import javax.swing.text.html.parser.DTD;

import oracle.net.aso.a;
import oracle.net.aso.i;
import oracle.net.aso.l;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.db.DatabaseMgr;
import com.heso.service.article.entity.ArticleAndvideoREturnObject;
import com.heso.service.article.entity.ArticleCommentDTO;
import com.heso.service.comment.entity.CommentServiceReturnObject;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.mall.entity.RecommendProducts;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.utility.ErrorProcess;
 

import data.DataRow;
import data.DataTable;

 



public class ArticleService {
	private static final Log logger = LogFactory.getLog(ArticleService.class);
	
	
	      String countVideoComment(String videoId){
		
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		String count= "";
		try {
			conn = dbm.getConnection();
			String sql = " ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(videoId);
		
			sql = "SELECT COUNT(hc.Id) FROM heso_comment AS hc,heso_designer_class AS hdc where hc.VIDEO_ID = ? AND hc.VIDEO_ID = hdc.ID";
				 
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			count  = dt1.getRows().get(0).getString("COUNT(hc.Id)");
			
			
			 
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}finally{

			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		return count;
		
	}
	
	    /**
		 * 获取免费专家课堂
		 * @param designerId
		 * @return
		 */
		public   ArticleAndvideoREturnObject getFreeVideo(String classId,String type){
			DatabaseMgr dbm = DatabaseMgr.getInstance();
			Connection conn = null;
			DataRow dr = null; 
			ArticleAndvideoREturnObject aavro = new ArticleAndvideoREturnObject();
			ArrayList<ArticleCommentDTO> acDtoList = new ArrayList<ArticleCommentDTO>();
			try {
				conn = dbm.getConnection(); 
				String sql = "SELECT hdc.flag, hd.desc as admindesc,hd.admin_id,hdc.id,hdc.IS_YUGAO,hdc.BELONG_SERVICE,hdc.READ_COUNT,hdc.IMGDESC,hdc.DESC,hdc.videoname," +
						"hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.name,hd.image " +
						"FROM heso_designer_class as hdc,heso_admin as hd WHERE  hdc.designerid = hd.admin_id AND hdc.flag='0' ";
				ArrayList<Object> argsList = new ArrayList<Object>();
				if(!"".equals(classId.trim())){
					sql = sql + " AND hdc.id = '" +
							classId +
							"'" ;
				}
				if(!"".equals(type.trim())){
					sql = sql + " AND hdc.videotype = '" +
							type +
							"'" ;
				}
				
				sql = sql + " ORDER BY hdc.createtime ASC";
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int n = dt.getRows().size();
				if(dt.getRows().size()<=0){
					logger.error(">>>>>>>>>查询专家课堂失败");
					throw new Exception("");
				} 
				for(int i=0;i<dt.getRows().size();i++){
					ArticleCommentDTO dto = new ArticleCommentDTO();
					String designerName = dt.getRows().get(i).getString("name");
					String id = dt.getRows().get(i).getString("id");
	 				String adminId = dt.getRows().get(i).getString("admin_id");
	 				String count  = countVideoComment(id);
					String videonName = dt.getRows().get(i).getString("videoname");
					String videoSrc = dt.getRows().get(i).getString("videosrc");
					String like = dt.getRows().get(i).getString("like");
					String videoLength = dt.getRows().get(i).getString("videolength");
					String videoImage = dt.getRows().get(i).getString("videoimage");
					String image = dt.getRows().get(i).getString("image");
					String createTime = dt.getRows().get(i).getString("createTime");
					
					String admindesc = dt.getRows().get(i).getString("admindesc");
					String imgdesc = dt.getRows().get(i).getString("imgdesc"); 
					String desc = dt.getRows().get(i).getString("desc");
					String flag = dt.getRows().get(i).getString("flag");
					String yugao = dt.getRows().get(i).getString("IS_YUGAO");
					String belongService = dt.getRows().get(i).getString("BELONG_SERVICE");
					int yuedu = dt.getRows().get(i).getInt("READ_COUNT");
					dto.setDesignerName(designerName);
					dto.setAdminId(adminId);
					dto.setImage(image);
					dto.setLike(like);
					dto.setvideoId(id);
					dto.setCount(count);
					dto.setvideoImage(videoImage);
					dto.setvideoLength(videoLength);
					dto.setvideonName(videonName);
					dto.setvideoSrc(videoSrc);
					dto.setDcreateTime(createTime);
					dto.setImgdesc(imgdesc);
					dto.setDesc(desc);
					dto.setIsYugao(yugao);
					dto.setBelongService(belongService);
					dto.setRead(yuedu);
					dto.setAdminDesc(admindesc);
					dto.setFlag(flag);
					acDtoList.add(dto);
					
				}
				 aavro.setAcDtoList(acDtoList);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}finally{

				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		return aavro;
		}
	    
		/**
		 * 根据Id查视频套餐
		 * @param id
		 * @param account
		 * @param productId
		 * @return
		 */
		public    ArticleAndvideoREturnObject getVideoById(String videoId,String account){
			DatabaseMgr dbm = DatabaseMgr.getInstance();
			Connection conn = null;
 			ArticleAndvideoREturnObject aavro = new ArticleAndvideoREturnObject();
			ArrayList<ArticleCommentDTO> acDtoList = new ArrayList<ArticleCommentDTO>();
			try {
				conn = dbm.getConnection(); 
				String sql = "SELECT  hmp.BAOHAN,hmp.ID ,hmp.NAME,hmp.IMAGE,hmp.DESC ,hmp.PRICE , hmp.SUPPLY_IMAGE,hmp.TUPIANMIAOSHU,hmp.START_TIME FROM heso_member_product  as hmp WHERE hmp.type = '3' AND hmp.BAOHAN LIKE '%" +
						videoId +
						"%'";
				ArrayList<Object> argsList = new ArrayList<Object>();
			 
				argsList.clear();
				 
		 
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				List<ArticleCommentDTO> memberList = new ArrayList<>();
				for(int i = 0;i<dtt.getRows().size();i++){
					DataRow dRow = dtt.getRows().get(i);
					ArticleCommentDTO dto = new ArticleCommentDTO();
					dto.setProductId(dRow.getString("ID"));
					dto.setBaohan(dRow.getString("BAOHAN"));
					dto.setVideonName(dRow.getString("NAME"));
					dto.setVideoImage(dRow.getString("IMAGE"));
					dto.setDesc(dRow.getString("DESC"));
					dto.setImage(dRow.getString("SUPPLY_IMAGE"));
					dto.setImgdesc(dRow.getString("TUPIANMIAOSHU"));
					dto.setStartime (dRow.getString("START_TIME"));
					dto.setPrice(dRow.getString("PRICE"));
					memberList.add(dto);
				}
				sql = "SELECT QUANYI FROM heso_account_quanyi WHERE  type = '3' and STATUS = '1' AND accounts LIKE '%" +
						account +
						"%'";
				argsList.clear();
				DataTable dtt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				for(ArticleCommentDTO aDto:memberList){
					for (int i = 0; i < dtt2.getRows().size(); i++) {
						String id = dtt2.getRows().get(i).getString("QUANYI");
						if(id.equals(aDto.getProductId())){
							aDto.setIsBuy("1");
						}else {
							if(aDto.getIsBuy()!=null&&!aDto.getIsBuy().equals("1")){
								aDto.setIsBuy("0");
							}						
						}
					}
				}
				sql = "SELECT baohan FROM heso_member_product WHERE id = (SELECT quanyi FROM heso_account_quanyi WHERE type = '33' and  accounts like '%" +
						account +
						"%')";
				argsList.clear();
				DataTable dataTable = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(dataTable.getRows().size()!=0){
					String baohan = dataTable.getRows().get(0).getString("baohan");
					sql = "SELECT id FROM heso_member_product WHERE id in (" +
							baohan +
							")";
					argsList.clear();
					DataTable data = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					int dataint = data.getRows().size();
					for(ArticleCommentDTO aDto:memberList){
						for (int i = 0; i < data.getRows().size(); i++) {
							String id = data.getRows().get(i).getString("id");
							if(id.equals(aDto.getProductId())){
								aDto.setIsBuy("1");
							}else {
								if(aDto.getIsBuy()!=null&&!aDto.getIsBuy().equals("1")){
									aDto.setIsBuy("0");
								}
							}
						}
					}
				}
				
				for(ArticleCommentDTO aDto:memberList){
					ArticleCommentDTO articleCommentDTO = new ArticleCommentDTO();
					List<ArticleCommentDTO> classList = new ArrayList<>();
					sql = "SELECT hdc.flag, hd.desc as admindesc,hd.admin_id,hdc.id,hdc.IS_YUGAO,hdc.BELONG_SERVICE,hdc.READ_COUNT,hdc.IMGDESC,hdc.DESC,hdc.videoname," +
							"hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.name,hd.image " +
							"FROM heso_designer_class as hdc,heso_admin as hd WHERE  hdc.designerid = hd.admin_id AND hdc.id in ( " +
							aDto.getBaohan() +
							")ORDER BY hdc.createtime DESC";
					DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					for(int i=0;i<dt.getRows().size();i++){
						ArticleCommentDTO dto = new ArticleCommentDTO();
						String designerName = dt.getRows().get(i).getString("name");
						String id = dt.getRows().get(i).getString("id");
		 				String adminId = dt.getRows().get(i).getString("admin_id");
		 				String count  = countVideoComment(id);
						String videonName = dt.getRows().get(i).getString("videoname");
						String videoSrc = dt.getRows().get(i).getString("videosrc");
						String like = dt.getRows().get(i).getString("like");
						String videoLength = dt.getRows().get(i).getString("videolength");
						String videoImage = dt.getRows().get(i).getString("videoimage");
						String image = dt.getRows().get(i).getString("image");
						String createTime = dt.getRows().get(i).getString("createTime");
						
						String admindesc = dt.getRows().get(i).getString("admindesc");
						String imgdesc = dt.getRows().get(i).getString("imgdesc"); 
						String desc = dt.getRows().get(i).getString("desc");
						String flag = dt.getRows().get(i).getString("flag");
						String yugao = dt.getRows().get(i).getString("IS_YUGAO");
						String belongService = dt.getRows().get(i).getString("BELONG_SERVICE");
						int yuedu = dt.getRows().get(i).getInt("READ_COUNT");
						
						dto.setDesignerName(designerName);
						dto.setAdminId(adminId);
						dto.setImage(image);
						dto.setLike(like);
						dto.setvideoId(id);
						dto.setCount(count);
						dto.setvideoImage(videoImage);
						dto.setvideoLength(videoLength);
						dto.setvideonName(videonName);
						dto.setvideoSrc(videoSrc);
						dto.setDcreateTime(createTime);
						dto.setImgdesc(imgdesc);
						dto.setDesc(desc);
						dto.setIsYugao(yugao);
						dto.setBelongService(belongService);
						dto.setRead(yuedu);
						dto.setAdminDesc(admindesc);
						dto.setFlag(flag);
						classList.add(dto);
					}
					if("1".equals(aDto.getIsBuy())){
						articleCommentDTO.setIsBuy("1");
					}else {
						articleCommentDTO.setIsBuy("0");
					}
					articleCommentDTO.setVideonName(aDto.getVideonName());
					articleCommentDTO.setVideoImage(aDto.getVideoImage());
					articleCommentDTO.setDesc(aDto.getDesc());
					articleCommentDTO.setImage(aDto.getImage());
					articleCommentDTO.setImgdesc(aDto.getImgdesc());
					articleCommentDTO.setStartime(aDto.getStartime());
					articleCommentDTO.setPrice(aDto.getPrice());
					articleCommentDTO.setDtolist(classList);
					articleCommentDTO.setProductId(aDto.getProductId());
					acDtoList.add(articleCommentDTO);
				}
				
				
			 
					/*sql = "SELECT hdc.flag, hd.desc as admindesc,hd.admin_id,hdc.id,hdc.IS_YUGAO,hdc.BELONG_SERVICE,hdc.READ,hdc.IMGDESC,hdc.DESC,hdc.videoname," +
							"hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.admin_name,hd.image " +
							"FROM heso_designer_class as hdc,heso_admin as hd " +
							"WHERE hdc.designerid = ? AND  hdc.designerid = hd.admin_id AND hdc.flag='0' ORDER BY hdc.createtime DESC";
					 argsList.clear();
					argsList.add(designerId);*/
			
				 aavro.setAcDtoList(acDtoList);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}finally{

				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		return aavro;
		}
	    
		
		 /**
		 * 获取收费专家课堂
		 * @param designerId
		 * @return
		 */
		public    ArticleAndvideoREturnObject getNotFreeVideo(String designerId,String account,String productId){
			DatabaseMgr dbm = DatabaseMgr.getInstance();
			Connection conn = null;
			DataRow dr = null;
			ArticleAndvideoREturnObject aavro = new ArticleAndvideoREturnObject();
			ArrayList<ArticleCommentDTO> acDtoList = new ArrayList<ArticleCommentDTO>();
			try {
				conn = dbm.getConnection(); 
				String sql = "SELECT  hmp.BAOHAN,hmp.ID ,hmp.NAME,hmp.IMAGE,hmp.DESC ,hmp.PRICE , hmp.SUPPLY_IMAGE,hmp.TUPIANMIAOSHU,hmp.START_TIME FROM heso_member_product  as hmp WHERE hmp.type = '3'";
				ArrayList<Object> argsList = new ArrayList<Object>();
				if(!productId.equals("")&&!productId.isEmpty()){
					sql = "SELECT  hmp.BAOHAN,hmp.ID ,hmp.NAME,hmp.IMAGE,hmp.DESC ,hmp.PRICE , hmp.SUPPLY_IMAGE,hmp.TUPIANMIAOSHU,hmp.START_TIME FROM heso_member_product  as hmp WHERE hmp.type = '3' and hmp.id= ?";
					argsList.clear();
					argsList.add(productId);
				}
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				List<ArticleCommentDTO> memberList = new ArrayList<>();
				for(int i = 0;i<dtt.getRows().size();i++){
					DataRow dRow = dtt.getRows().get(i);
					ArticleCommentDTO dto = new ArticleCommentDTO();
					dto.setProductId(dRow.getString("ID"));
					dto.setBaohan(dRow.getString("BAOHAN"));
					dto.setVideonName(dRow.getString("NAME"));
					dto.setVideoImage(dRow.getString("IMAGE"));
					dto.setDesc(dRow.getString("DESC"));
					dto.setImage(dRow.getString("SUPPLY_IMAGE"));
					dto.setImgdesc(dRow.getString("TUPIANMIAOSHU"));
					dto.setStartime (dRow.getString("START_TIME"));
					dto.setPrice(dRow.getString("PRICE"));
					memberList.add(dto); 
				}
				sql = "SELECT QUANYI FROM heso_account_quanyi WHERE  type = '3' and STATUS = '1' AND accounts LIKE '%" +
						account +
						"%'";
				argsList.clear();
				DataTable dtt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				for(ArticleCommentDTO aDto:memberList){
					for (int i = 0; i < dtt2.getRows().size(); i++) {
						String id = dtt2.getRows().get(i).getString("QUANYI");
						if(id.equals(aDto.getProductId())){
							aDto.setIsBuy("1");
						}else {
							if(aDto.getIsBuy()!=null&&!aDto.getIsBuy().equals("1")){
								aDto.setIsBuy("0");
							}						
						}
					}
				}
				sql = "SELECT baohan FROM heso_member_product WHERE id = (SELECT quanyi FROM heso_account_quanyi WHERE type = '33' and  accounts like '%" +
						account +
						"%')";
				argsList.clear();
				DataTable dataTable = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(dataTable.getRows().size()!=0){
					String baohan = dataTable.getRows().get(0).getString("baohan");
					sql = "SELECT id FROM heso_member_product WHERE id in (" +
							baohan +
							")";
					argsList.clear();
					DataTable data = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					int dataint = data.getRows().size();
					for(ArticleCommentDTO aDto:memberList){
						for (int i = 0; i < data.getRows().size(); i++) {
							String id = data.getRows().get(i).getString("id");
							if(id.equals(aDto.getProductId())){
								aDto.setIsBuy("1");
							}else {
								if(aDto.getIsBuy()!=null&&!aDto.getIsBuy().equals("1")){
									aDto.setIsBuy("0");
								}
							}
						}
					}
				}
				
				for(ArticleCommentDTO aDto:memberList){
					ArticleCommentDTO articleCommentDTO = new ArticleCommentDTO();
					List<ArticleCommentDTO> classList = new ArrayList<>();
					sql = "SELECT hdc.flag, hd.desc as admindesc,hd.admin_id,hdc.id,hdc.IS_YUGAO,hdc.BELONG_SERVICE,hdc.READ_COUNT,hdc.IMGDESC,hdc.DESC,hdc.videoname," +
							"hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.name,hd.image " +
							"FROM heso_designer_class as hdc,heso_admin as hd WHERE  hdc.designerid = hd.admin_id AND hdc.id in ( " +
							aDto.getBaohan() +
							")ORDER BY hdc.createtime ASC";
					DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					for(int i=0;i<dt.getRows().size();i++){
						ArticleCommentDTO dto = new ArticleCommentDTO();
						String designerName = dt.getRows().get(i).getString("name");
						String id = dt.getRows().get(i).getString("id");
		 				String adminId = dt.getRows().get(i).getString("admin_id");
		 				String count  = countVideoComment(id);
						String videonName = dt.getRows().get(i).getString("videoname");
						String videoSrc = dt.getRows().get(i).getString("videosrc");
						String like = dt.getRows().get(i).getString("like");
						String videoLength = dt.getRows().get(i).getString("videolength");
						String videoImage = dt.getRows().get(i).getString("videoimage");
						String image = dt.getRows().get(i).getString("image");
						String createTime = dt.getRows().get(i).getString("createTime");
						
						String admindesc = dt.getRows().get(i).getString("admindesc");
						String imgdesc = dt.getRows().get(i).getString("imgdesc"); 
						String desc = dt.getRows().get(i).getString("desc");
						String flag = dt.getRows().get(i).getString("flag");
						String yugao = dt.getRows().get(i).getString("IS_YUGAO");
						String belongService = dt.getRows().get(i).getString("BELONG_SERVICE");
						int yuedu = dt.getRows().get(i).getInt("READ_COUNT");
						
						dto.setDesignerName(designerName);
						dto.setAdminId(adminId);
						dto.setImage(image);
						dto.setLike(like);
						dto.setvideoId(id);
						dto.setCount(count);
						dto.setvideoImage(videoImage);
						dto.setvideoLength(videoLength);
						dto.setvideonName(videonName);
						dto.setvideoSrc(videoSrc);
						dto.setDcreateTime(createTime);
						dto.setImgdesc(imgdesc);
						dto.setDesc(desc);
						dto.setIsYugao(yugao);
						dto.setBelongService(belongService);
						dto.setRead(yuedu);
						dto.setAdminDesc(admindesc);
						dto.setFlag(flag);
						classList.add(dto);
					}
					if("1".equals(aDto.getIsBuy())){
						articleCommentDTO.setIsBuy("1");
					}else {
						articleCommentDTO.setIsBuy("0");
					}
					articleCommentDTO.setVideonName(aDto.getVideonName());
					articleCommentDTO.setVideoImage(aDto.getVideoImage());
					articleCommentDTO.setDesc(aDto.getDesc());
					articleCommentDTO.setImage(aDto.getImage());
					articleCommentDTO.setImgdesc(aDto.getImgdesc());
					articleCommentDTO.setStartime(aDto.getStartime());
					articleCommentDTO.setPrice(aDto.getPrice());
					articleCommentDTO.setProductId(aDto.getProductId());
					articleCommentDTO.setDtolist(classList);
					acDtoList.add(articleCommentDTO);
				}
				
				
			 
					/*sql = "SELECT hdc.flag, hd.desc as admindesc,hd.admin_id,hdc.id,hdc.IS_YUGAO,hdc.BELONG_SERVICE,hdc.READ,hdc.IMGDESC,hdc.DESC,hdc.videoname," +
							"hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.admin_name,hd.image " +
							"FROM heso_designer_class as hdc,heso_admin as hd " +
							"WHERE hdc.designerid = ? AND  hdc.designerid = hd.admin_id AND hdc.flag='0' ORDER BY hdc.createtime DESC";
					 argsList.clear();
					argsList.add(designerId);*/
			
				 aavro.setAcDtoList(acDtoList);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}finally{

				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		return aavro;
		}
		
	public  MallServiceReturnObject getVideoProducts(String vedioId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		Map<String, List<String>> map = new HashMap<>();
		
		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String  sql = "SELECT ID, VALUE,LABEL,NAME FROM  heso_type WHERE KEYWORD = 'style' ";
			DataTable dtt = dbm.execSqlTrans(sql, argsList, conn);
			int d = dtt.getRows().size();
			for(int i = 0;i<dtt.getRows().size();i++){
				List<String> list = new ArrayList<>();
				String value = dtt.getRows().get(i).getString("VALUE");
				String id = dtt.getRows().get(i).getString("ID");
					String label = dtt.getRows().get(i).getString("LABEL");
				String name = dtt.getRows().get(i).getString("NAME"); 
		
			
				list.add(name.trim());
				list.add(value.trim());					
				
				list.add(label.trim());
				map.put(id, list);

			}
			sql = "SELECT * FROM heso_designer_class WHERE id = ?";
			argsList.clear();
			argsList.add(vedioId);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			String productIds = dt.getRows().get(0).getString("PRODUCTS");
			argsList.clear();
			sql = "SELECT * FROM heso_product WHERE product_id in (" +
					productIds +
					")";
			DataTable dttt = dbm.execSqlTrans(sql, argsList, conn);
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();

			if(dttt.getRows().size()!=0){
				for (int i = 0; i < dttt.getRows().size(); i++) {
					DataRow dr = dttt.getRows().get(i);
					ProductItemObject pioSuit = new ProductItemObject();
					pioSuit.setProductId(dr.getString("product_id"));
					pioSuit.setName(dr.getString("name"));
					pioSuit.setDesc(dr.getString("desc"));
					pioSuit.setCategory(dr.getString("category"));
					pioSuit.setDesignName(dr.getString("designer_name"));
					pioSuit.setScene(dr.getString("scene"));
					pioSuit.setType(dr.getString("type"));
					String sstyle = dr.getString("style");
					String sex = dr.getString("sex");
					List<String> list = getStyleName(sstyle, map,sex);
					pioSuit.setStyle(list.get(0)); 
					pioSuit.setAgeType(dr.getString("age_type"));
					pioSuit.setShape(dr.getString("shape"));
					pioSuit.setPrice(dr.getString("price"));
					pioSuit.setDiscountPrice(dr.getString("discount_price"));
					pioSuit.setSuitPrice(dr.getString("suit_price"));
					pioSuit.setImgFront(dr.getString("img_front"));
					pioSuit.setImgBehind(dr.getString("img_behind"));
					pioSuit.setImages(dr.getString("images"));
					pioSuit.setDesignerId(dr.getString("DESIGNER"));
					pioSuit.setSex(dr.getString("sex"));
					// 获取套装明细
					sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
					argsList.clear();
					argsList.add(pioSuit.getProductId());
					DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
					boolean status = true;
					for (int j = 0; j < dtGoods.getRows().size(); j++) {
						DataRow dr1 = dtGoods.getRows().get(j);
						ProductItemObject pioGoods = new ProductItemObject();
						pioGoods.setProductId(dr1.getString("product_id"));
						pioGoods.setName(dr1.getString("name"));
						pioGoods.setDesc(dr1.getString("desc"));
						pioGoods.setCategory(dr1.getString("category"));
						pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
						pioGoods.setSupplyName(dr1.getString("supply_name"));
						pioGoods.setColor(dr1.getString("color"));
						pioGoods.setPrice(dr1.getString("price"));
						pioGoods.setSuitPrice(dr1.getString("suit_price"));
						pioGoods.setDiscountPrice(dr1.getString("discount_price"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoods.setStockCount(dr1.getString("stock_count"));
						pioGoods.setSoldCount(dr1.getString("sold_count"));
						pioGoods.setImgFront(dr1.getString("img_front"));
						pioGoods.setImgBehind(dr1.getString("img_behind"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoodsList.add(pioGoods);
						if(dr1.getString("stock_status").equals("0")){
							status = false;
						}
					}
					if(status){
						pioSuit.setStockStatus("1");
					}
					// 添加到单品列表
					pioSuit.setGoodsList(pioGoodsList);
					// 添加套装列表
					pioSuitList.add(pioSuit);
				}
			}
			msro.setPioList(pioSuitList);
			
 		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return msro;
	}
	
	
	public   MallServiceReturnObject getRcommenedProducts(){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		MallServiceReturnObject msro = new MallServiceReturnObject();
		/*MallServiceReturnObject manmsro = new MallServiceReturnObject();
		MallServiceReturnObject womenmsro = new MallServiceReturnObject();*/
		Connection conn = DatabaseMgr.getInstance().getConnection();
		Map<String, List<String>> map = new HashMap<>();
		List<RecommendProducts> manreList = new ArrayList<>();
		List<RecommendProducts> womenreList = new ArrayList<>();
		try {
			
			
			ArrayList<Object> argsList = new ArrayList<Object>();
			String  sql = "";
 			 
			for(int i = 0;i<2;i++){
				for(int j = 0;j<10;j++){
					ArrayList<ProductItemObject> mansuit = new ArrayList<ProductItemObject>();
					ArrayList<ProductItemObject> womensuit = new ArrayList<ProductItemObject>();
					RecommendProducts manre = new RecommendProducts();
					RecommendProducts womenre = new RecommendProducts();
					
					sql = "SELECT * FROM heso_recommend_products WHERE styleid = ? and sex = ?";
					argsList.clear();
					argsList.add(j);
					argsList.add(i);
					DataTable dtDataTable = dbm.execSqlTrans(sql, argsList, conn);
					if(dtDataTable.getRows().size()==0){
						continue;
					}
					String sex = i +"";
					String styleName = dtDataTable.getRows().get(0).getString("stylename");
					String styleId = j+"";
					String productId = dtDataTable.getRows().get(0).getString("productid");
					argsList.clear();
					sql = "SELECT * FROM heso_product WHERE product_id in (" +
							productId +
							")";
					DataTable dttt = dbm.execSqlTrans(sql, argsList, conn);
					if(dttt.getRows().size()!=0){
						for (int z = 0; z < dttt.getRows().size(); z++) {
							DataRow dr = dttt.getRows().get(z);
							ProductItemObject pioSuit = new ProductItemObject();
							pioSuit.setProductId(dr.getString("product_id"));
							pioSuit.setName(dr.getString("name"));
							pioSuit.setDesc(dr.getString("desc"));
							pioSuit.setCategory(dr.getString("category"));
							pioSuit.setDesignName(dr.getString("designer_name"));
							pioSuit.setScene(dr.getString("scene"));
							pioSuit.setType(dr.getString("type"));
							String sstyle = dr.getString("style");
 							List<String> listt = getStyleName(sstyle, map,sex);
							pioSuit.setStyle(listt.get(0)); 
							pioSuit.setAgeType(dr.getString("age_type"));
							pioSuit.setShape(dr.getString("shape"));
							pioSuit.setPrice(dr.getString("price"));
							pioSuit.setDiscountPrice(dr.getString("discount_price"));
							pioSuit.setSuitPrice(dr.getString("suit_price"));
							pioSuit.setImgFront(dr.getString("img_front"));
							pioSuit.setImgBehind(dr.getString("img_behind"));
							pioSuit.setImages(dr.getString("images"));
							pioSuit.setDesignerId(dr.getString("DESIGNER"));
							pioSuit.setSex(dr.getString("sex"));
						
						
							// 添加套装列表
							if(sex.trim().equals("1")){
								mansuit.add(pioSuit);
							}else if(sex.trim().equals("0")){
								womensuit.add(pioSuit);
							}
						}																		
					}
					// 
					if(sex.trim().equals("1")){
						manre.setList(mansuit);
						manre.setSex(sex);
						manre.setStyleName(styleName);
						manre.setStyleId(styleId);
						manreList.add(manre);
					}else if(sex.trim().equals("0")){
						womenre.setList(womensuit);
						womenre.setSex(sex);
						womenre.setStyleId(styleId);
						womenre.setStyleName(styleName);
						womenreList.add(womenre);
					}
				}
		}
			msro.setManreList(manreList);
			msro.setWomenreList(womenreList);
			
			
			
 		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return msro;
	}
	
	public  static  List<String> getStyleName(String ids,Map<String, List<String>> map,String sex){
		List<String> list = new ArrayList<>();
		String wstyle1 ="";
		String wstyle2 ="";
		String[] sourceStrArray = ids.split(",");
 		for (int i = 0; i < sourceStrArray.length; i++) {
 			 String styleId = sourceStrArray[i].trim();
 			 for(String key:map.keySet()){
 				 if(styleId.equals(key)){
 					 List<String> ddList = map.get(key);
 					if(wstyle1.equals("")||wstyle1.isEmpty()){
 						if(sex.equals("1")){
 							wstyle1 = map.get(key).get(1);										
 						}else {
 							wstyle1 = map.get(key).get(0);										
						}
 						wstyle2 = map.get(key).get(2);		
 					}else {
 						if(sex.equals("1")){
 							wstyle1 = wstyle1 + " | " +map.get(key).get(1);	
 						}else {
 							wstyle1 = wstyle1 + " | " +map.get(key).get(0);	
						}
 						wstyle2 = wstyle2 + " " +map.get(key).get(2);		
 					}
 					
 				 }
 			 }
 		}
 		list.add(wstyle1);
		list.add(wstyle2);
		return list;
	}
	    
	/**
	 * 获取专家课堂
	 * @param designerId
	 * @return
	 */
	public   ArticleAndvideoREturnObject getVideo(String designerId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		ArticleAndvideoREturnObject aavro = new ArticleAndvideoREturnObject();
		ArrayList<ArticleCommentDTO> acDtoList = new ArrayList<ArticleCommentDTO>();
		try {
			conn = dbm.getConnection(); 
			String sql = "";
			ArrayList<Object> argsList = new ArrayList<Object>();
			if("".equals(designerId)){
				sql = "SELECT hd.admin_id,hdc.id,hdc.videoname,hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.admin_name,hd.image " +
						"FROM heso_designer_class as hdc,heso_admin as hd " +
						"WHERE  hdc.designerid = hd.admin_id";
			}else {
				
				sql = "SELECT hd.admin_id,hdc.id,hdc.videoname,hdc.videolength,hdc.videosrc,hdc.like,hdc.videoimage,hdc.createtime,hd.admin_name,hd.image " +
						"FROM heso_designer_class as hdc,heso_admin as hd " +
						"WHERE hdc.designerid = ? AND hdc.designerid = hd.admin_id";
				 
				argsList.add(designerId);
			}
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int n = dt.getRows().size();
			if(dt.getRows().size()<=0){
				logger.error(">>>>>>>>>查询专家课堂失败");
				throw new Exception("");
			} 
			for(int i=0;i<dt.getRows().size();i++){
				ArticleCommentDTO dto = new ArticleCommentDTO();
				String designerName = dt.getRows().get(i).getString("admin_name");
				String id = dt.getRows().get(i).getString("id");
 				String adminId = dt.getRows().get(i).getString("admin_id");
 				String count  = countVideoComment(id);
				String videonName = dt.getRows().get(i).getString("videoname");
				String videoSrc = dt.getRows().get(i).getString("videosrc");
				String like = dt.getRows().get(i).getString("like");
				String videoLength = dt.getRows().get(i).getString("videolength");
				String videoImage = dt.getRows().get(i).getString("videoimage");
				String image = dt.getRows().get(i).getString("image");
				String createTime = dt.getRows().get(i).getString("createTime");
				dto.setDesignerName(designerName);
				dto.setAdminId(adminId);
				dto.setImage(image);
				dto.setLike(like);
				dto.setvideoId(id);
				dto.setCount(count);
				dto.setvideoImage(videoImage);
				dto.setvideoLength(videoLength);
				dto.setvideonName(videonName);
				dto.setvideoSrc(videoSrc);
				dto.setDcreateTime(createTime);
				acDtoList.add(dto);
			}
			 aavro.setAcDtoList(acDtoList);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}finally{

			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	return aavro;
	}
	
	/**
	 * 视频点赞
	 * @param videoId
	 * @return
	 */
	public   ArticleAndvideoREturnObject addLikeTovideo(String videoId){
		ArticleAndvideoREturnObject aavro = new ArticleAndvideoREturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = "UPDATE heso_designer_class as hdc SET hdc.like = hdc.like+1 where ID=?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(videoId);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return aavro;
	
	}
	
	public    String  getVideoURL(String videoId,String passcode){
		String url  = "";
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = "SELECT * FROM heso_designer_class WHERE id = ? AND passcode = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(videoId);
			argsList.add(passcode);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(dt.getRows().size()>=0){
				url = dt.getRows().get(0).getString("videosrc");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return url;
	
	}
	
	public  ArticleAndvideoREturnObject getCommentByvideoId(String videoID,String accounts){

		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null; 
		DataRow dr = null;
		ArticleAndvideoREturnObject aavro = new ArticleAndvideoREturnObject();
		ArrayList<ArticleCommentDTO> acDtoList = new ArrayList<ArticleCommentDTO>();
		try {
			conn = dbm.getConnection();
			String sql = "SELECT hc.id,hc.good_count,hc.ACCOUNT,hc.USER_ID,hc.CREATE_TIME,hc.COMMENT ,ha.image from heso_comment as hc ,heso_account as ha where video_ID =? AND ha.ACCOUNT=hc.ACCOUNT order BY hc.CREATE_TIME DESC;";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(videoID);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int n = dt.getRows().size();
 			if(dt.getRows().size() <= 0 )  
				throw new Exception("001095");
 			String flag = "0";
			for(int i = 0 ;i<dt.getRows().size();i++){
				ArticleCommentDTO dto = new ArticleCommentDTO();
				String account = dt.getRows().get(i).getString("account");
				if(account.equals(accounts)){
					flag = "1";
				}
				String userName = dt.getRows().get(i).getString("user_id");
				String createTime = dt.getRows().get(i).getString("create_time");
				String comment = dt.getRows().get(i).getString("comment");
				String 	image = dt.getRows().get(i).getString("image");
				String goodCount = dt.getRows().get(i).getString("good_count");
				String commentId = dt.getRows().get(i).getString("id");
				dto.setaccount(account);
				dto.setAccountName(userName);
				dto.setDcreateTime(createTime);
				dto.setComment(comment);
				dto.setImage(image);
				dto.setCommentId(commentId);
				dto.setGoodCount(goodCount);
				String isAddGood = "0";
				if(!accounts.equals("")){
					sql = "SELECT * FROM heso_comment_good WHERE account = ? AND comment_id = ?";
					argsList.clear();
					argsList.add(accounts);
					argsList.add(commentId);
					DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtt.getRows().size()>0){
						isAddGood = "1";
					}
				}
				dto.setIsAddGood(isAddGood);
				acDtoList.add(dto);
			}
			aavro.setFlag(flag);
			aavro.setAcDtoList(acDtoList);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	return aavro;
	}
	/**
	 * 文章点赞
	 */
	public void addGood(String articleId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = "UPDATE heso_article_infor SET good_count = good_count+1 where ARTICLE_ID=?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(articleId);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 点赞
	 * 
	 * @param Id
	 * @return
	 */
	public CommentServiceReturnObject addGoodComment(String Id,String ip,String account,String videoId) {
		CommentServiceReturnObject csro = new CommentServiceReturnObject();
		try {
			String sql = "update heso_comment set good_count = good_count+1 where id=?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(Id);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
			
			sql = "INSERT heso_comment_good (ACCOUNT,USER_IP,COMMENT_ID,TYPE,VIDEO_ID) VALUES (?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(ip);
			argsList.add(Id);
			argsList.add("1");
			argsList.add(videoId);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
			
		} catch (Exception e) {
			csro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return csro;
	}
	
	public  void addCommentByvideoId(String videoID,String comment,String userId,String account){
		
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = "insert into heso_comment (TYPE,video_ID,ACCOUNT,USER_ID,COMMENT,COMMENT_ID)VALUES(?,?,?,?,?,?)";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add("1");
			argsList.add(videoID);
			argsList.add(account);
			argsList.add(userId);
			argsList.add(comment);
			argsList.add(videoID);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 添加评论
	 * @param articleId
	 * @param comment
	 * @param userId
	 * @param account
	 */
	
	public  void addComment(String articleId,String comment,String userId,String account){
		
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = "insert into heso_comment (TYPE,ARTICLE_ID,ACCOUNT,USER_ID,COMMENT,COMMENT_ID)VALUES(?,?,?,?,?,?)";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add("1");
			argsList.add(articleId);
			argsList.add(account);
			argsList.add(userId);
			argsList.add(comment);
			argsList.add(articleId);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public   void getArticleList(){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = "SELECT a1.ARTICLE_ID,a2.ARTICLE_CONTENT  FROM heso_article_infor a1,heso_article_infor_detail a2,heso_comment c where a1.ARTICLE_ID=a2.ARTICLE_ID AND a1.ARTICLE_ID = c.article_id";
			ArrayList<Object> argsList = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
 			if(dt.getRows().size() <= 0 )
				throw new Exception("101268");
 			
 			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr1 = dt.getRows().get(i);
				String articleId =dr1.getString("ARTICLE_ID");
				String comment = dr1.getString("ARTICLE_CONTENT");
				}
			String [] orderIds = dt.getRows().get(0).getString("ARTICLE_ID").split(";");
		
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {  
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		//addGood("1");
		//addComment("1", "testetesttsetse", "fdf", "fhdjhfjd");
		//ArrayList<ArticleCommentDTO>   aCommentDTOs = getVideo("1111111");
		//addLikeTovideo("0");
		//ArticleAndvideoREturnObject vaa = getCommentByvideoId("0");
		//addCommentByvideoId("0", "hhh", "爸爸在", "0000000000000019");
		//ArticleAndvideoREturnObject a = getVideo("1111111");
		//String count = countVideoComment("6");
		//ArticleAndvideoREturnObject  aCommentDTOs=getNotFreeVideo("","ww","");
		//MallServiceReturnObject msro = getRcommenedProducts();
		//String string = getVideoURL("6", "662323");
		//MallServiceReturnObject d =getRcommenedProducts();
		System.out.println("---------");
	}
}
