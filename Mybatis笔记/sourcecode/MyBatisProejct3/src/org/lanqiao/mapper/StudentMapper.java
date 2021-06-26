package org.lanqiao.mapper;

import java.util.List;

import org.lanqiao.entity.Student;

//操作Mybatis的接口
public interface StudentMapper {
		/*
		 * 1.方法名和mapper.xml文件中标签的id值相同
		 * 2.方法的 输入参数 和mapper.xml文件中标签的 parameterType类型一致
		 * 3.方法的返回值  和mapper.xml文件中标签的 resultType类型一致
		 * 
		 */
//		public abstract Student  queryStudentByStuno(int stuno);
		Student  queryStudentByStuno(int stuno);
		//查询全部
		List<Student> queryAllStudents();
		//增加
		void addStudentWithConverter(Student student);
		
		
		void addStudent(Student student);
		//删除
		void deleteStudentByStuno(int stuno);
		
		//修改
		void updateStudentByStuno(Student student);
		
		
		Student queryStudentByStunoWithConverter(int stuno);
		
}
