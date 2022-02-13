package com.erin.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 过滤敏感词的工具类
 * \
 */

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符，检测到敏感符号后替换成'***'
    private static final String REPLACEMENT = "***";

    // 前缀树
    private class TrieNode {

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 当前节点的子节点(key是下级节点的字符,value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }

    // 根节点
    private TrieNode rootNode = new TrieNode();

    /*
    * 当容器实例化SensitiveFilter bean，且在调用其构造器后，该方法会被执行
    * 而bean在服务启动的时候就初始化好了
    * 该方法的作用是根据敏感词（存在sensitive-words.txt中）来构造树
    * */
    @PostConstruct
    public void init() {
        try (
                // getClassLoader获取类加载器，类加载器从类路径下加载资源，类路径即~/target/classes路径
                // 得到字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点,进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1，指向敏感词构成的前缀树的节点
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (begin < text.length()) {
            if (position < text.length()) {
                // 此时posiotion和begin指向同一个字符
                char c = text.charAt(position);

                // 跳过特殊的符号，不作处理
                if (isSymbol(c)) {
                    // 若指针1处于根节点，会将此符号计入结果,让指针2向下走一步
                    if (tempNode == rootNode) {
                        sb.append(c);
                        begin++;
                    }
                    // 无论特殊符号在敏感字符串的开头或中间,指针3都向下走一步
                    position++;
                    continue;
                }

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                // 以begin开头的字符串不是敏感词
                if (tempNode == null) {
                    sb.append(text.charAt(begin));
                    // position进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = rootNode;
                } else if (tempNode.isKeywordEnd()) {
                    // 发现敏感词,将begin~position字符串替换掉
                    sb.append(REPLACEMENT);
                    // begin进入下一个位置
                    begin = ++position;
                    // 重新指向根节点
                    tempNode = rootNode;
                } else {
                    // 以begin开头、position结尾的字符串可能是敏感词的前缀，因此需要检查下一个字符
                    position++;
                }
            }else { // posiotion遍历越界但仍未匹配到敏感词
                /*
                * 之前的写法会丢掉结尾的敏感词
                * 例子：已知敏感词有fabcd和abc，要核验的字符串的最后一段是fabc，此时指针二指f，指针三指到c,
                * 根据if中的判断，以f为开头，c为结尾的字符串并不是敏感词，因为以c为value的节点的isKeywordEnd属性为false, position++,
                * 按照之前的写法，此时position就跳出循环（while）, 然后将fabc加到结果StringBuilder中,
                * 但abc这个敏感词没有被过滤掉，因此在此处做出改进，posiotion越界并不是停止循环的条件，begin才是
                * */
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // position进入下一个位置，如果begin没有越界，则position回退到语句`if (position < text.length())`继续寻找敏感词
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            }
        }

        return sb.toString();
    }

    /**
     * 判断是否为符号
     * @param c
     * @return
     */
    private boolean isSymbol(Character c) {
        // isAsciiAlphanumeric方法判断字符是不是合法的、普通的字符，0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

}
