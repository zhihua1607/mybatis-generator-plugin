/*
 * Copyright (c) 2017.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itfsw.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.tools.AbstractShellCallback;
import com.itfsw.mybatis.generator.plugins.tools.DBHelper;
import com.itfsw.mybatis.generator.plugins.tools.ObjectUtil;
import com.itfsw.mybatis.generator.plugins.tools.SqlHelper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ---------------------------------------------------------------------------
 *
 * ---------------------------------------------------------------------------
 * @author: hewei
 * @time:2017/6/29 13:34
 * ---------------------------------------------------------------------------
 */
public class SelectSelectivePluginTest {
    private DBHelper helper;

    /**
     * 初始化
     * @throws IOException
     * @throws SQLException
     */
    @Before
    public void init() throws IOException, SQLException {
        helper = DBHelper.getHelper("scripts/SelectSelectivePlugin/init.sql");
    }

    /**
     * 测试生成的方法
     * @throws Exception
     */
    @Test
    public void testSelectByExampleSelective() throws Exception {
        DBHelper.cleanDao();
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(Resources.getResourceAsStream("scripts/SelectSelectivePlugin/mybatis-generator.xml"));

        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, new AbstractShellCallback(true) {
            @Override
            public void reloadProject(ClassLoader loader) {
                SqlSession sqlSession = null;
                try {
                    // 1. 测试sql
                    sqlSession = helper.getSqlSession();
                    ObjectUtil tbMapper = new ObjectUtil(sqlSession.getMapper(loader.loadClass("com.itfsw.mybatis.generator.plugins.dao.TbMapper")));

                    ObjectUtil tbExample = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.TbExample");
                    ObjectUtil criteria = new ObjectUtil(tbExample.invoke("createCriteria"));
                    criteria.invoke("andIdLessThan", 100l);
                    tbExample.set("orderByClause", "field2 asc");

                    ObjectUtil columnField1 = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.Tb$Column#field1");
                    // java 动态参数不能有两个会冲突，最后一个封装成Array!!!必须使用反射创建指定类型数组，不然调用invoke对了可变参数会检查类型！
                    Object columns1 = Array.newInstance(columnField1.getCls(), 1);
                    Array.set(columns1, 0, columnField1.getObject());

                    String sql = SqlHelper.getFormatMapperSql(tbMapper.getObject(), "selectByExampleSelective", tbExample.getObject(), columns1);
                    Assert.assertEquals(sql, "select field1 from tb WHERE (  id < '100' )  order by field2 asc");

                    ObjectUtil columnField2 = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.Tb$Column#field2");
                    Object columns2 = Array.newInstance(columnField1.getCls(), 2);
                    Array.set(columns2, 0, columnField1.getObject());
                    Array.set(columns2, 1, columnField2.getObject());

                    sql = SqlHelper.getFormatMapperSql(tbMapper.getObject(), "selectByExampleSelective", tbExample.getObject(), columns2);
                    Assert.assertEquals(sql, "select field1 ,  field2 from tb WHERE (  id < '100' )  order by field2 asc");


                    // 2. 执行sql
                    List list = (List) tbMapper.invokeVarArgs("selectByExampleSelective", tbExample.getObject(), columns1);
                    Assert.assertEquals(list.size(), 3);
                    int index = 0;
                    for (Object obj : list) {
                        ObjectUtil objectUtil = new ObjectUtil(obj);
                        // 没有查询这两个字段
                        if (objectUtil.get("id") != null || objectUtil.get("field2") != null) {
                            Assert.assertTrue(false);
                        }
                        if (index == 0) {
                            Assert.assertEquals(objectUtil.get("field1"), "fd1");
                        } else if (index == 1) {
                            Assert.assertNull(objectUtil.get("field1"));
                        } else {
                            Assert.assertEquals(objectUtil.get("field1"), "fd3");
                        }

                        index++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.assertTrue(false);
                } finally {
                    sqlSession.close();
                }
            }
        }, warnings);
        myBatisGenerator.generate(null, null, null, true);
    }

    /**
     * 测试生成的方法
     * @throws Exception
     */
    @Test
    public void testSelectByPrimaryKeySelective() throws Exception {
        DBHelper.cleanDao();
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(Resources.getResourceAsStream("scripts/SelectSelectivePlugin/mybatis-generator.xml"));

        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, new AbstractShellCallback(true) {
            @Override
            public void reloadProject(ClassLoader loader) {
                SqlSession sqlSession = null;
                try {
                    // 1. 测试sql
                    sqlSession = helper.getSqlSession();
                    ObjectUtil tbMapper = new ObjectUtil(sqlSession.getMapper(loader.loadClass("com.itfsw.mybatis.generator.plugins.dao.TbMapper")));

                    ObjectUtil columnField1 = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.TbKeys$Column#field1");
                    // java 动态参数不能有两个会冲突，最后一个封装成Array!!!必须使用反射创建指定类型数组，不然调用invoke对了可变参数会检查类型！
                    Object columns1 = Array.newInstance(columnField1.getCls(), 1);
                    Array.set(columns1, 0, columnField1.getObject());

                    String sql = SqlHelper.getFormatMapperSql(tbMapper.getObject(), "selectByPrimaryKeySelective", 1, columns1);
                    Assert.assertEquals(sql, "select field1 from tb where id = 1");

                    // 2. 测试xxxKey
                    ObjectUtil tbKeysMapper = new ObjectUtil(sqlSession.getMapper(loader.loadClass("com.itfsw.mybatis.generator.plugins.dao.TbKeysMapper")));
                    ObjectUtil tbKeysKey = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.TbKeysKey");
                    tbKeysKey.set("key1", 1l);
                    tbKeysKey.set("key2", "2");

                    ObjectUtil columnField2 = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.TbKeys$Column#field2");
                    // java 动态参数不能有两个会冲突，最后一个封装成Array!!!必须使用反射创建指定类型数组，不然调用invoke对了可变参数会检查类型！
                    Object columns2 = Array.newInstance(columnField2.getCls(), 1);
                    Array.set(columns2, 0, columnField2.getObject());

                    sql = SqlHelper.getFormatMapperSql(tbKeysMapper.getObject(), "selectByPrimaryKeySelective", tbKeysKey.getObject(), columns2);
                    Assert.assertEquals(sql, "select field2 from tb_keys where key1 = 1 and key2 = '2'");

                    // 3. 执行sql
                    Object tbKeys = tbKeysMapper.invokeVarArgs("selectByPrimaryKeySelective", tbKeysKey.getObject(), columns1);
                    Assert.assertEquals(new ObjectUtil(tbKeys).get("field1"), "fd1");

                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.assertTrue(false);
                } finally {
                    sqlSession.close();
                }
            }
        }, warnings);
        myBatisGenerator.generate(null, null, null, true);
    }

    /**
     * 测试生成的方法
     * @throws Exception
     */
    @Test
    public void testSelectOneByExampleSelective() throws Exception {
        // 没有配置SelectOneByExamplePlugin插件时不生成对应方法
        DBHelper.cleanDao();
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(Resources.getResourceAsStream("scripts/SelectSelectivePlugin/mybatis-generator.xml"));
        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
        myBatisGenerator.generate(null, null, null, false);
        List<GeneratedJavaFile> list = myBatisGenerator.getGeneratedJavaFiles();
        for (GeneratedJavaFile file : list) {
            if (file.getFileName().equals("TbMapper.java")) {
                Assert.assertFalse(file.getFormattedContent().matches(".*selectByExampleSelective.*"));
            }
        }

        // 配置了SelectOneByExamplePlugin
        DBHelper.cleanDao();
        config = cp.parseConfiguration(Resources.getResourceAsStream("scripts/SelectSelectivePlugin/mybatis-generator-with-SelectOneByExamplePlugin.xml"));

        myBatisGenerator = new MyBatisGenerator(config, new AbstractShellCallback(true) {
            @Override
            public void reloadProject(ClassLoader loader) {
                SqlSession sqlSession = null;
                try {
                    // 1. 测试sql
                    sqlSession = helper.getSqlSession();
                    ObjectUtil tbMapper = new ObjectUtil(sqlSession.getMapper(loader.loadClass("com.itfsw.mybatis.generator.plugins.dao.TbMapper")));

                    ObjectUtil tbExample = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.TbExample");
                    ObjectUtil criteria = new ObjectUtil(tbExample.invoke("createCriteria"));
                    criteria.invoke("andIdEqualTo", 3l);
                    tbExample.set("orderByClause", "field2 asc");

                    ObjectUtil columnField1 = new ObjectUtil(loader, "com.itfsw.mybatis.generator.plugins.dao.model.Tb$Column#field1");
                    // java 动态参数不能有两个会冲突，最后一个封装成Array!!!必须使用反射创建指定类型数组，不然调用invoke对了可变参数会检查类型！
                    Object columns1 = Array.newInstance(columnField1.getCls(), 1);
                    Array.set(columns1, 0, columnField1.getObject());

                    String sql = SqlHelper.getFormatMapperSql(tbMapper.getObject(), "selectOneByExampleSelective", tbExample.getObject(), columns1);
                    Assert.assertEquals(sql, "select field1 from tb WHERE (  id = '3' )  order by field2 asc limit 1");

                    // 2. 执行sql
                    Object result = tbMapper.invokeVarArgs("selectOneByExampleSelective", tbExample.getObject(), columns1);
                    ObjectUtil tb = new ObjectUtil(result);
                    Assert.assertEquals(tb.get("field1"), "fd3");
                    Assert.assertNull(tb.get("field2"));

                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.assertTrue(false);
                } finally {
                    sqlSession.close();
                }
            }
        }, warnings);
        myBatisGenerator.generate(null, null, null, true);
    }

    @AfterClass
    public static void clean() {
        DBHelper.reset();
    }
}