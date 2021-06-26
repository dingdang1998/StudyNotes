package org.lanqiao.test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.lanqiao.entity.Student;
import org.lanqiao.mapper.StudentMapper;

public class Test {
		//查询单个学生（使用了转换器）
		public static void queryStudentByStunoWithConverter() throws IOException {
			//Connection -  SqlSession操作MyBatis
					//conf.xml - > reader
					Reader reader = Resources.getResourceAsReader("conf.xml") ;
					//reader  ->SqlSession
					
					//可以通过build的第二参数 指定数据库环境
					SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
					SqlSession session = sessionFacotry.openSession() ;
					
					StudentMapper studentMapper = session.getMapper(StudentMapper.class) ;
					Student student = studentMapper.queryStudentByStunoWithConverter(1) ;//接口中的方法->SQL语句
					
					System.out.println(student);
					session.close();
		}

	//查询单个学生
	public static void queryStudentByStuno() throws IOException {
		//Connection -  SqlSession操作MyBatis
				//conf.xml - > reader
				Reader reader = Resources.getResourceAsReader("conf.xml") ;
				//reader  ->SqlSession
				
				//可以通过build的第二参数 指定数据库环境
				SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
				SqlSession session = sessionFacotry.openSession() ;
				
				StudentMapper studentMapper = session.getMapper(StudentMapper.class) ;
				Student student = studentMapper.queryStudentByStuno(2) ;//接口中的方法->SQL语句
				
				System.out.println(student);
				session.close();
	}
	
	  //查询全部学生
 		public static void queryAllStudents() throws IOException {
			//Connection -  SqlSession操作MyBatis
					//conf.xml - > reader
					Reader reader = Resources.getResourceAsReader("conf.xml") ;
					//reader  ->SqlSession
					//可以通过build的第二参数 指定数据库环境
					SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
					SqlSession session = sessionFacotry.openSession() ;
					
					
//					List<Student> students = session.selectList(statement ) ;
					StudentMapper studentMapper = session.getMapper( StudentMapper.class) ;
					List<Student> students = studentMapper.queryAllStudents() ;//接口的方法->SQL
					
					System.out.println(students);
					session.close();
		}
		
 		
 		 //增加学生
 		public static void addStudent() throws IOException {
 			//Connection -  SqlSession操作MyBatis
					//conf.xml - > reader
					Reader reader = Resources.getResourceAsReader("conf.xml") ;
					//reader  ->SqlSession
					//可以通过build的第二参数 指定数据库环境
					SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
					SqlSession session = sessionFacotry.openSession() ;
					
//					String statement = "org.lanqiao.entity.studentMapper."+"addStudent";
					Student student = new Student(13,"ww3",23,"s3");
					
					
//					int count = session.insert(statement, student );//statement：指定执行的SQL    student：sql中需要的参数 （ ? ? ? ）
					StudentMapper studentMapper = session.getMapper(StudentMapper.class);
					studentMapper.addStudent(student);
					
					session.commit(); //提交事务
					
					System.out.println("增加成功");
					session.close();
		}
 		
 		 //增加学生（带转换器）
 		public static void addStudentWithConverter() throws IOException {
 			//Connection -  SqlSession操作MyBatis
					//conf.xml - > reader
					Reader reader = Resources.getResourceAsReader("conf.xml") ;
					//reader  ->SqlSession
					//可以通过build的第二参数 指定数据库环境
					SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
					SqlSession session = sessionFacotry.openSession() ;
					
//					String statement = "org.lanqiao.entity.studentMapper."+"addStudent";
					Student student = new Student(63,"ww53",23,"s3");
					student.setStuSex(true);//1
					
					
//					int count = session.insert(statement, student );//statement：指定执行的SQL    student：sql中需要的参数 （ ? ? ? ）
					StudentMapper studentMapper = session.getMapper(StudentMapper.class);
					studentMapper.addStudentWithConverter(student);
					
					session.commit(); //提交事务
					
					System.out.println("增加成功");
					session.close();
		}
 		
 		 //删除学生
 		public static void delteStudentByStuno() throws IOException {
 			//Connection -  SqlSession操作MyBatis
					//conf.xml - > reader
					Reader reader = Resources.getResourceAsReader("conf.xml") ;
					//reader  ->SqlSession
					//可以通过build的第二参数 指定数据库环境
					SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
					SqlSession session = sessionFacotry.openSession() ;
					
//					String statement = "org.lanqiao.entity.studentMapper."+"deleteStudentByStuno";
//					
//					int count = session.delete(statement,3) ;
					StudentMapper studentMapper = session.getMapper(StudentMapper.class);
					studentMapper.deleteStudentByStuno(13);
					
					session.commit(); //提交事务
					
					System.out.println("删除成功");
					session.close();
		}
	
 		 //修改学生
 		public static void updateStudentByStuno() throws IOException {
 			//Connection -  SqlSession操作MyBatis
					//conf.xml - > reader
					Reader reader = Resources.getResourceAsReader("conf.xml") ;
					//reader  ->SqlSession
					//可以通过build的第二参数 指定数据库环境
					SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
					SqlSession session = sessionFacotry.openSession() ;
					
//					String statement = "org.lanqiao.entity.studentMapper."+"updateStudentByStuno";
					//修改的参数
					Student student = new Student();
					//修改哪个人，where stuno =2 
//					student.setStuNo(2);
					//修改成什么样子？
					student.setStuName("ls");
					student.setStuAge(24);
					student.setGraName("s1");
					//执行
//					int count = session.update(statement,student) ;
					StudentMapper studentMapper = session.getMapper(StudentMapper.class);
					studentMapper.updateStudentByStuno(student);
					
					session.commit(); //提交事务
					
					System.out.println("修改成功");
					session.close();
		}	
	
	public static void main(String[] args) throws IOException {
//		queryStudentByStunoWithConverter();
		queryStudentByStuno();
//		addStudentWithConverter();
//		queryAllStudents();
//		addStudent();
//		delteStudentByStuno();
//		updateStudentByStuno();
//		queryAllStudents();
	}
}