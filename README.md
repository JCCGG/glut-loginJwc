# 桂林理工大学教务处客户端

实验功能：获取登录验证码、检查验证码、登录教务处、查成绩、查课程表、查考试安排、简单学籍信息

#### 登录验证码

public byte[] getCheckCode()

#### 检查验证码

public boolean checkedCode(String code)

| 参数 |         描述     |
| ---- | ---------------- |
| code | 需要检查的验证码 |



#### 登录

public boolean loginCheck(String code, String username, String password)

| 参数     | 类型   | 描述                         |
| -------- | ------ | ---------------------------- |
| code     | String | getCheckCode()中获取的验证码 |
| username | String | 教务账号                     |
| password | String | 教务密码                     |

#### 查询成绩

public ArrayList<StudentScore> getStudentScore(String year, String term)

| 参数 | 类型   | 描述                   |
| ---- | ------ | ---------------------- |
| year | String | 36,37（表示2016,2017） |
| term | String | 1,2（春，秋）          |



#### 查询课程表

public ArrayList<StudentCourse> getStudentCurrentCourse(String year, String term) 

| 参数 | 类型   | 描述                   |
| ---- | ------ | ---------------------- |
| year | String | 36,37（表示2016,2017） |
| term | String | 1,2（春，秋）          |

#### 查询考试安排

public ArrayList<StudentExam> getStudentAllExam()

#### 查询基本学籍信息

public StudentInfo getStudentInfo() 
