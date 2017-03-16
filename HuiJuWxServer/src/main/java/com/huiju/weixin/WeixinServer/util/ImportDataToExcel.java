package com.huiju.weixin.WeixinServer.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.huiju.weixin.WeixinServer.model.KeyTb;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 导入数据到Excel表中
 * @author rcfad
 *
 */
public class ImportDataToExcel {
	
	public static  void execute(List<KeyTb> keyList,String xlsPath) throws IOException, RowsExceededException, WriteException{
		 xlsPath=System.getProperty("user.dir")+"/wx_device_qrcode"+new Date().getTime()+".xls";
		//  打开文件      
        WritableWorkbook book  =  Workbook.createWorkbook( new  File( xlsPath ));     
        //  生成名为“sheet1”的工作表，参数0表示这是第一页      
        WritableSheet sheet  =  book.createSheet("sheet1", 0);     
        //  在Label对象的构造子中指名单元格位置是第一列第一行(0,0)     
        //  以及单元格内容为test      
        Label label  = new Label(0,0," DeviceID" );     
        Label label1 = new Label(1,0,"内容");
        Label label2 = new Label(2,0,"序号");
        //  将定义好的单元格添加到工作表中      
        sheet.addCell(label);
        sheet.addCell(label1);
        sheet.addCell(label2);
        /**/ /*    
        *循环写入到xls中 
         */     
        for(int i=0;i<keyList.size();i++){
        	  label  =  new Label(0,i+1,keyList.get(i).getDeviceId() );     
              label1 =  new Label(1,i+1,keyList.get(i).getTicketId());
              label2 =  new Label(2,i+1,keyList.get(i).getQrcodeSerial());
              //将定义好的单元格添加到工作表中      
              sheet.addCell(label);
              sheet.addCell(label1);
              sheet.addCell(label2);
        }
        //  写入数据并关闭文件      
        book.write();     
       book.close();     
	}
}
