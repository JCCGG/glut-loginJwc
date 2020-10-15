package cn.wwdab.client;

import cn.wwdab.entity.*;
import cn.wwdab.utils.StrUtils;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


//教务处

public class LoginJwc {

    private CloseableHttpClient client = null;
    private final BasicCookieStore basicCookieStore;
    private boolean isLogin=false;//是否已经登录

    public LoginJwc() {
//        构建可以自定义存储cookie的httpclient
        basicCookieStore = new BasicCookieStore();
        this.client = HttpClients.custom().setDefaultCookieStore(basicCookieStore).build();
    }

    public boolean isLogin() {
        return isLogin;
    }

    //*************************	查询考试安排


    public ArrayList<StudentExam> getStudentAllExam() {
        CloseableHttpResponse response = null;
        ArrayList<StudentExam> studentExamsList=null;
        try {

            URI uri = new URI("http://jw.glut.edu.cn/academic/manager/examstu/studentQueryAllExam.do");
            URIBuilder uriBuilder = new URIBuilder(uri);

//			添加请求参数get
            ArrayList<NameValuePair> getList = new ArrayList<NameValuePair>();
            getList.add(new BasicNameValuePair("pagingPageVLID", "1"));//页码
            getList.add(new BasicNameValuePair("pagingNumberPerVLID", "30"));//每页条数
            getList.add(new BasicNameValuePair("sortDirectionVLID", "-1"));//排序
            getList.add(new BasicNameValuePair("sortColumnVLID", "s.examRoom.exam.endTime"));
            uriBuilder.setParameters(getList);


            HttpGet httpget = new HttpGet(uriBuilder.build());
//			请求头
            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpget.addHeader("Host", "jw.glut.edu.cn");

//			获取考试信息
            response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            if(entity!=null){
                studentExamsList = new ArrayList<StudentExam>();
                String bodyStr = EntityUtils.toString(entity);

//			筛选考试数据
                Document doc = Jsoup.parse(bodyStr);
                Elements select = doc.select(".datalist>tbody>tr");

                int row = 1;
                for (Element element : select) {
                    StudentExam studentExam = new StudentExam();
                    if (row == 1) {
//					不筛选第一行
                        row = 0;
                        continue;
                    } else {
                        studentExam.setCourseNum(element.select("td").get(0).text());//课程号
                        studentExam.setCourseName(element.select("td").get(1).text());//课程名称
                        studentExam.setExamTime(element.select("td").get(2).text());//考试时间
                        studentExam.setExamSite(element.select("td").get(3).text());//考试地点
                        studentExam.setExamNature(element.select("td").get(4).text());//考试性质
                        studentExamsList.add(studentExam);
                    }

                }
                System.out.println(studentExamsList);
                httpget.releaseConnection();

            }


        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return studentExamsList;

    }

//*************************	查询课程表

    public ArrayList<StudentCourse> getStudentCurrentCourse(String year, String term) {
        CloseableHttpResponse response = null;
        ArrayList<StudentCourse> studentCourses=null; //所有课的课表信息，每个元素为StudentCourse对象

        try {

            URI uri = new URI("http://jw.glut.edu.cn/academic/student/currcourse/currcourse.jsdo");
            URIBuilder uriBuilder = new URIBuilder(uri);

//			添加请求参数get
            ArrayList<NameValuePair> getList = new ArrayList<NameValuePair>();
            getList.add(new BasicNameValuePair("year", year));
            getList.add(new BasicNameValuePair("term", term));
            uriBuilder.setParameters(getList);


            HttpGet httpget = new HttpGet(uriBuilder.build());
//			请求头
            httpget.addHeader("Referer", uriBuilder.build().toString());
            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpget.addHeader("Host", "jw.glut.edu.cn");

//			获取课表
            response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            if(entity!=null){
                studentCourses = new ArrayList<StudentCourse>();
                String bodyStr = EntityUtils.toString(entity);

    //			筛选课表数据
                Document doc = Jsoup.parse(bodyStr);
                Element form = doc.select(".infolist_tab").get(0);//获取第一个表
                Elements select = form.select(".infolist_common");
    //			把课表放入ArrayList数组
                for (Element element : select) {
    //				创建javaBean
                    StudentCourse studentCourse = new StudentCourse();
    //				将课程数据写入javaBean
                    studentCourse.setCourseNum(element.select("td").get(0).text());//课程号
                    studentCourse.setCourseName(element.select("td").get(2).text());//课程名称
                    studentCourse.setCourseTeacherName(element.select("td").get(3).text());//课程任课老师
                    studentCourse.setCourseScore(element.select("td").get(4).text());//课程学分

                    ArrayList<StudentCourseInfo> StudentCourseFormList = new ArrayList<StudentCourseInfo>();//存储所有的行
                    Elements FormData = element.select("td").get(9).select("tr");//所有课程的课程表

                    for (Element oneCourseFormData : FormData) {
    //					一门课的课表

                        StudentCourseInfo studentCourseForm = new StudentCourseInfo();
                        studentCourseForm.setWeek(oneCourseFormData.select("td").get(0).text());
                        studentCourseForm.setOneWeek(oneCourseFormData.select("td").get(1).text());
                        studentCourseForm.setCourseTime(oneCourseFormData.select("td").get(2).text());
                        studentCourseForm.setClassNum(oneCourseFormData.select("td").get(3).text());
                        StudentCourseFormList.add(studentCourseForm);
                    }
                    studentCourse.setCourseInfoList(StudentCourseFormList);//获取课表结束

                    studentCourses.add(studentCourse);//把一门课的课程信息存入以StudentCourse为对象创建的ArrayList 数组

                }

                System.out.println(studentCourses);

                httpget.releaseConnection();
            }

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return studentCourses;
    }


//*************************	查成绩

    public ArrayList<StudentScore> getStudentScore(String year, String term) {
        CloseableHttpResponse response = null;
        ArrayList<StudentScore> studentScoreList=null;
        try {

            URI uri = new URI("http://jw.glut.edu.cn/academic/manager/score/studentOwnScore.do");
            URIBuilder uriBuilder = new URIBuilder(uri);

//			添加请求参数get
            ArrayList<NameValuePair> getList = new ArrayList<NameValuePair>();
            getList.add(new BasicNameValuePair("groupId", ""));
            getList.add(new BasicNameValuePair("moduleId", "2020"));
            getList.add(new BasicNameValuePair("randomString", StrUtils.getRandomStr()));
            uriBuilder.setParameters(getList);

//			添加请求参数Post
            ArrayList<NameValuePair> postList = new ArrayList<NameValuePair>();
            postList.add(new BasicNameValuePair("year", year));
            postList.add(new BasicNameValuePair("term", term));
            postList.add(new BasicNameValuePair("prop", ""));
            postList.add(new BasicNameValuePair("groupName", ""));
            postList.add(new BasicNameValuePair("para", "0"));
            postList.add(new BasicNameValuePair("sortColumn", ""));
            postList.add(new BasicNameValuePair("Submit", "查询"));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(postList);


            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setEntity(urlEncodedFormEntity);
//			请求头
            httpPost.addHeader("Referer", uriBuilder.build().toString());
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpPost.addHeader("Host", "jw.glut.edu.cn");
            httpPost.addHeader("Origin", "http://jw.glut.edu.cn");


            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity!=null){
                studentScoreList = new ArrayList<StudentScore>();
                String bodyStr = EntityUtils.toString(entity);
    //			筛选成绩数据
                Document doc = Jsoup.parse(bodyStr);
                Elements select = doc.select(".datalist>tbody>tr");


    //			把成绩放入ArrayList数组

                int index = 1;
                for (Element element : select) {
                    StudentScore studentScore = new StudentScore();
                    if (index == 1) {
    //					不筛选第一行
                        index = 0;
                        continue;
                    } else {
    //					将成绩数据写入javaBean
                        studentScore.setYear(element.select("td").get(0).text());//学年
                        studentScore.setTerm(element.select("td").get(1).text());//学期
                        studentScore.setCollege(element.select("td").get(2).text());//开课学院
                        studentScore.setCourseNum(element.select("td").get(3).text());//课程号
                        studentScore.setCourseName(element.select("td").get(4).text());//课程名
                        studentScore.setTeacherName(element.select("td").get(6).text());//老师名
                        studentScore.setCommentGrade(element.select("td").get(7).text());//总评
                        studentScore.setPoint(element.select("td").get(8).text());//绩点
                        studentScore.setCredit(element.select("td").get(9).text());//学分
                        studentScore.setHour(element.select("td").get(10).text());//学时
                        studentScore.setMode(element.select("td").get(11).text());//考核方式
                        studentScore.setAttr(element.select("td").get(12).text());//选课属性
                        studentScore.setNature(element.select("td").get(14).text());//考试性质
                        studentScore.setAsk(element.select("td").get(16).text());//课程要求
                        studentScore.setFlags(element.select("td").get(20).text());//及格标志
                        studentScoreList.add(studentScore);
                    }

                }

                System.out.println(studentScoreList);
                httpPost.releaseConnection();
            }

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return studentScoreList;

    }

//***************************	学籍信息

    public StudentInfo getStudentInfo() {

        CloseableHttpResponse response = null;
        StudentInfo studentInfo=null;
        try {

            URI uri = new URI("http://jw.glut.edu.cn/academic/student/studentinfo/studentInfoModifyIndex.do");
            URIBuilder uriBuilder = new URIBuilder(uri);

//			添加请求参数
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
            arrayList.add(new BasicNameValuePair("frombase", "0"));
            arrayList.add(new BasicNameValuePair("wantTag", "0"));
            arrayList.add(new BasicNameValuePair("groupId", ""));
            arrayList.add(new BasicNameValuePair("moduleId", "2060"));
            arrayList.add(new BasicNameValuePair("randomString", StrUtils.getRandomStr()));
            uriBuilder.setParameters(arrayList);

            HttpGet httpGet = new HttpGet(uriBuilder.build());

            httpGet.addHeader("Referer", "http://jw.glut.edu.cn/academic/listLeft.do?randomString=HlcyjogZte");
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpGet.addHeader("Host", "jw.glut.edu.cn");

            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(entity!=null){
                studentInfo = new StudentInfo();
                String bodyStr = EntityUtils.toString(entity);

    //			筛选学籍信息
                Document doc = Jsoup.parse(bodyStr);
                Elements select = doc.select(".form>tbody>tr");

                studentInfo.setStudentNum(select.get(0).select("td").get(0).text());//学号
                studentInfo.setStudentName(select.get(1).select("td").get(0).text());//姓名
                studentInfo.setStudentBirthday(select.get(3).select("td").get(0).text());//生日
                studentInfo.setStudentNative(select.get(4).select("td").get(1).text());//籍贯
                studentInfo.setStudentIDNum(select.get(5).select("td").get(1).text());//身份证
                studentInfo.setStudentNation(select.get(6).select("td").get(0).text());//民族
                studentInfo.setStudentPolities(select.get(6).select("td").get(1).text());//政治面貌
                studentInfo.setStudentScore(select.get(14).select("td").get(0).text());//高考分数
                studentInfo.setStudentClass(select.get(22).select("td").get(0).text());//班级
                studentInfo.setStudentCanpus(select.get(23).select("td").get(0).text());//校区


                System.out.println(studentInfo);
                httpGet.releaseConnection();
            }

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return studentInfo;


    }

    //	获取前置cookie
    public boolean openJWC() {
        boolean flags = false;
        CloseableHttpResponse response = null;
        String url = "http://jw.glut.edu.cn/academic/common/security/login.jsp";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        try {
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                flags = true;

            } else {
                System.out.println("访问教务处失败！");
            }
            httpGet.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flags;
    }


    //不需要验证码登录验证
    public boolean login(String username, String password) {

        boolean flags = false;
        CloseableHttpResponse response = null;
        if (openJWC()) {
//		获取cookie
            List<Cookie> cookies = basicCookieStore.getCookies();
            Cookie cookie = cookies.get(0);
//        System.out.println(cookie.getValue());
            String url = "http://jw.glut.edu.cn/academic/j_acegi_security_check;jsessionid=" + cookie.getValue();
            HttpPost httpPost = new HttpPost(url);
//		添加请求头
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpPost.addHeader("Referer", "http://jw.glut.edu.cn/academic/common/security/login.jsp");
            httpPost.addHeader("Host", "jw.glut.edu.cn");
            httpPost.addHeader("Origin", "http://jw.glut.edu.cn");

//		构建登录请求体
            List<NameValuePair> bodyList = new ArrayList<NameValuePair>();
            bodyList.add(new BasicNameValuePair("j_username", username));
            bodyList.add(new BasicNameValuePair("j_password", password));

            try {
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(bodyList);
                httpPost.setEntity(urlEncodedFormEntity);

                response = client.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                String value = response.getFirstHeader("Location").getValue();
                System.out.println(value);
                if (statusCode == 302 && "http://jw.glut.edu.cn/academic/index_new.jsp".equals(value)) {
//		    	登录成功
                    flags = true;
                    this.isLogin=true;
                }

                httpPost.releaseConnection();

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (response != null)
                        response.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        return flags;

    }

    //需要验证码登录验证
    public boolean loginCheck(String code, String username, String password) {
        boolean flags = false;
        CloseableHttpResponse response = null;
        String url = "http://jw.glut.edu.cn/academic/j_acegi_security_check";
        HttpPost httpPost = new HttpPost(url);
//		添加请求头
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        httpPost.addHeader("Referer", "http://jw.glut.edu.cn/academic/common/security/login.jsp");
        httpPost.addHeader("Host", "jw.glut.edu.cn");
        httpPost.addHeader("Origin", "http://jw.glut.edu.cn");

//		构建登录请求体
        List<NameValuePair> bodyList = new ArrayList<NameValuePair>();
        bodyList.add(new BasicNameValuePair("j_username", username));
        bodyList.add(new BasicNameValuePair("j_password", password));
        bodyList.add(new BasicNameValuePair("j_captcha", code));

        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(bodyList);
            httpPost.setEntity(urlEncodedFormEntity);

            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String value = response.getFirstHeader("Location").getValue();
            System.out.println(value);
            if (statusCode == 302 && "http://jw.glut.edu.cn/academic/index_new.jsp".equals(value)) {
//		    	登录成功
                flags = true;
                this.isLogin=true;
            }
            httpPost.releaseConnection();


        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return flags;

    }

    //	验证验证码是否正确
    public boolean checkedCode(String code) {
        boolean flags = false;
        CloseableHttpResponse response = null;
//		构建URI
        System.out.println("验证码：" + code);

        try {
            URI uri = new URI("http://jw.glut.edu.cn/academic/checkCaptcha.do");
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("captchaCode", code);
//		创建get请求
            HttpPost httpPost = new HttpPost(uriBuilder.build());
//		添加请求头
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpPost.addHeader("Referer", "http://jw.glut.edu.cn/academic/common/security/login.jsp");
            httpPost.addHeader("Host", "jw.glut.edu.cn");
            httpPost.addHeader("Origin", "http://jw.glut.edu.cn");


//			获取验证结果
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity);
            flags = Boolean.parseBoolean(body);

            httpPost.releaseConnection();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        return flags;

    }

    //	获取验证码
    public byte[] getCheckCode() {
        CloseableHttpResponse response = null;
        byte[] byteArray = null;
        try {
//			构建URI
            URI uri = new URI("http://jw.glut.edu.cn/academic/getCaptcha.do");

//			创建get请求
            HttpGet httpGet = new HttpGet(uri);
//			添加请求头
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpGet.addHeader("Referer", "http://jw.glut.edu.cn/academic/common/security/login.jsp");
            httpGet.addHeader("Host", "jw.glut.edu.cn");


            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            byteArray = EntityUtils.toByteArray(entity);

            System.out.println("获取验证码完毕！");
            httpGet.releaseConnection();

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return byteArray;
    }

    //	获取验证码返回BufferedIamge
    public BufferedImage getCheckCodeToBufferedImage() {
        CloseableHttpResponse response = null;
        BufferedImage bufferedImage = null;
        try {
//			构建URI
            URI uri = new URI("http://jw.glut.edu.cn/academic/getCaptcha.do");

//			创建get请求
            HttpGet httpGet = new HttpGet(uri);
//			添加请求头
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            httpGet.addHeader("Referer", "http://jw.glut.edu.cn/academic/common/security/login.jsp");
            httpGet.addHeader("Host", "jw.glut.edu.cn");

            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();

//            将字节数组转为BufferedIamge
            BufferedImage read = ImageIO.read(content);
            bufferedImage = ImageHelper.convertImageToBinary(read);

//            把验证码写入本地方便观察
            File file = new File("src/main/resources/checkCode.jpg");
            ImageIO.write(bufferedImage,"jpg",file);

            System.out.println("获取验证码完毕！");
            httpGet.releaseConnection();

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bufferedImage;
    }


}
