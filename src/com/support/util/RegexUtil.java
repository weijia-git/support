package com.support.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * 
 * @author itboy
 *
 */
public class RegexUtil {

	/**
	 * 匹配图象
	 *
	 *
	 * 格式: /相对路径/文件名.后缀 (后缀为gif,dmp,png)
	 *
	 * 匹配 : /forum/head_icon/admini2005111_ff.gif 或 admini2005111.dmp
	 *
	 *
	 * 不匹配: c:/admins4512.gif
	 *
	 */
	public static final String REGEXP_IMAGE = "^(/{0,1}//w){1,}//.(gif|dmp|png|jpg)$|^//w{1,}//.(gif|dmp|png|jpg)$";
	
	/**
	 * 匹配用户名
	 * 
	 * 格式：由大小写字母、数字、下划线组成且不能以数字开头
	 */
	public static final String REGEXP_USERNAME = "[a-zA-Z][0-9a-zA-Z_]{5,35}";
	
	/**
	 * 匹配email地址
	 *
	 *
	 * 格式: XXX@XXX.XXX.XX
	 *
	 * 匹配 : foo@bar.com 或 foobar@foobar.com.au
	 *
	 * 不匹配: foo@bar 或 $$$@bar.com
	 *
	 */
	public static final String REGEXP_EMAIL = "(?://w[-._//w]*//w@//w[-._//w]*//w//.//w{2,3}$)";

	/**
	 * 匹配匹配并提取url
	 *
	 *
	 * 格式: XXXX://XXX.XXX.XXX.XX/XXX.XXX?XXX=XXX
	 *
	 * 匹配 : http://www.suncer.com 或news://www
	 *
	 * 不匹配: c:/window
	 *
	 */
	public static final String REGEXP_URL = "(//w+)://([^/:]+)(://d*)?([^#//s]*)";

	/**
	 * 匹配并提取http
	 *
	 * 格式: http://XXX.XXX.XXX.XX/XXX.XXX?XXX=XXX 或 ftp://XXX.XXX.XXX 或
	 * https://XXX
	 *
	 * 匹配 : http://www.suncer.com:8080/index.html?login=true
	 *
	 * 不匹配: news://www
	 *
	 */
	public static final String REGEXP_HTTP = "(http|https|ftp)://([^/:]+)(://d*)?([^#//s]*)";

	/**
	 * 匹配日期
	 *
	 *
	 * 格式(首位不为0): XXXX-XX-XX或 XXXX-X-X
	 *
	 *
	 * 范围:1900--2099
	 *
	 *
	 * 匹配 : 2005-04-04
	 *
	 *
	 * 不匹配: 01-01-01
	 *
	 */
	public static final String DATE_BARS_REGEXP = "^((((19){1}|(20){1})\\d{2})|\\d{2})-[0,1]?\\d{1}-[0-3]?\\d{1}$";

	/**
	 * 匹配日期
	 *
	 *
	 * 格式: XXXX/XX/XX
	 *
	 *
	 * 范围:
	 *
	 *
	 * 匹配 : 2005/04/04
	 *
	 *
	 * 不匹配: 01/01/01
	 *
	 */
	public static final String DATE_SLASH_REGEXP = "^[0-9]{4}/(((0[13578]|(10|12))/(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)/(0[1-9]|[1-2][0-9]|30)))$";

	/**
	 * 匹配电话
	 *
	 *
	 * 格式为: 0XXX-XXXXXX(10-13位首位必须为0) 或0XXX XXXXXXX(10-13位首位必须为0) 或
	 *
	 * (0XXX)XXXXXXXX(11-14位首位必须为0) 或 XXXXXXXX(6-8位首位不为0) 或
	 * XXXXXXXXXXX(11位首位不为0)
	 *
	 *
	 * 匹配 : 0371-123456 或 (0371)1234567 或 (0371)12345678 或 010-123456 或
	 * 010-12345678 或 12345678912
	 *
	 *
	 * 不匹配: 1111-134355 或 0123456789
	 *
	 */
	public static final String REGEXP_PHONE_NUMBER = "^(?:0[0-9]{2,3}[-//s]{1}|//(0[0-9]{2,4}//))[0-9]{6,8}$|^[1-9]{1}[0-9]{5,7}$|^[1-9]{1}[0-9]{10}$";

	/**
	 * 匹配身份证
	 *
	 * 格式为: XXXXXXXXXX(10位) 或 XXXXXXXXXXXXX(13位) 或 XXXXXXXXXXXXXXX(15位) 或
	 * XXXXXXXXXXXXXXXXXX(18位)
	 *
	 * 匹配 : 0123456789123
	 *
	 * 不匹配: 0123456
	 *
	 */
	public static final String REGEXP_ID_NUMBER = "^//d{10}|//d{13}|//d{15}|//d{18}$";

	/**
	 * 匹配邮编代码
	 *
	 * 格式为: XXXXXX(6位)
	 *
	 * 匹配 : 012345
	 *
	 * 不匹配: 0123456
	 *
	 */
	public static final String ZIP_REGEXP = "^[0-9]{6}$";// 匹配邮编代码

	/**
	 * 不包括特殊字符的匹配 (字符串中不包括符号 数学次方号^ 单引号' 双引号" 分号; 逗号, 帽号: 数学减号- 右尖括号> 左尖括号< 反斜杠/
	 * 即空格,制表符,回车符等 )
	 *
	 * 格式为: x 或 一个一上的字符
	 *
	 * 匹配 : 012345
	 *
	 * 不匹配: 0123456 // ;,:-<>//s].+$";//
	 */
	public static final String NON_SPECIAL_CHAR_REGEXP = "^[^'/";
	// 匹配邮编代码

	/**
	 * 匹配非负整数（正整数 + 0)
	 */
	public static final String NON_NEGATIVE_INTEGERS_REGEXP = "^//d+$";

	/**
	 * 匹配不包括零的非负整数（正整数 > 0)
	 */
	public static final String NON_ZERO_NEGATIVE_INTEGERS_REGEXP = "^[1-9]+//d*$";

	/**
	 *
	 * 匹配正整数
	 *
	 */
	public static final String POSITIVE_INTEGER_REGEXP = "^[0-9]*[1-9][0-9]*$";

	/**
	 *
	 * 匹配非正整数（负整数 + 0）
	 *
	 */
	public static final String NON_POSITIVE_INTEGERS_REGEXP = "^((-//d+)|(0+))$";

	/**
	 *
	 * 匹配负整数
	 *
	 */
	public static final String NEGATIVE_INTEGERS_REGEXP = "^-[0-9]*[1-9][0-9]*$";

	/**
	 *
	 * 匹配整数
	 *
	 */
	public static final String INTEGER_REGEXP = "^-?//d+$";

	/**
	 *
	 * 匹配非负浮点数（正浮点数 + 0）
	 *
	 */
	public static final String NON_NEGATIVE_RATIONAL_NUMBERS_REGEXP = "^//d+(//.//d+)?$";

	/**
	 *
	 * 匹配正浮点数
	 *
	 */
	public static final String POSITIVE_RATIONAL_NUMBERS_REGEXP = "^(([0-9]+//.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*//.[0-9]+)|([0-9]*[1-9][0-9]*))$";

	/**
	 *
	 * 匹配非正浮点数（负浮点数 + 0）
	 *
	 */
	public static final String NON_POSITIVE_RATIONAL_NUMBERS_REGEXP = "^((-//d+(//.//d+)?)|(0+(//.0+)?))$";

	/**
	 *
	 * 匹配负浮点数
	 *
	 */
	public static final String NEGATIVE_RATIONAL_NUMBERS_REGEXP = "^(-(([0-9]+//.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*//.[0-9]+)|([0-9]*[1-9][0-9]*)))$";

	/**
	 *
	 * 匹配浮点数
	 *
	 */
	public static final String RATIONAL_NUMBERS_REGEXP = "^(-?//d+)(//.//d+)?$";

	/**
	 *
	 * 匹配由26个英文字母组成的字符串
	 *
	 */
	public static final String LETTER_REGEXP = "^[A-Za-z]+$";

	/**
	 *
	 * 匹配由26个英文字母的大写组成的字符串
	 *
	 */
	public static final String UPWARD_LETTER_REGEXP = "^[A-Z]+$";

	/**
	 *
	 * 匹配由26个英文字母的小写组成的字符串
	 *
	 */
	public static final String LOWER_LETTER_REGEXP = "^[a-z]+$";

	/**
	 *
	 * 匹配由数字和26个英文字母组成的字符串
	 *
	 */
	public static final String LETTER_NUMBER_REGEXP = "^[A-Za-z0-9]+$";

	/**
	 *
	 * 匹配由数字、26个英文字母或者下划线组成的字符串
	 *
	 */
	public static final String LETTER_NUMBER_UNDERLINE_REGEXP = "^//w+$";

	/**
	 * @param email
	 *            E Mail address
	 * @return boolean Flag demonstrating if it's a valid email
	 */
	public boolean isValidEmail(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

		isValid = matchPattern(expression, email);

		return isValid;
	}

	/**
	 * @param name
	 *            Person's first name
	 * @return boolean Flag indicating if it's a valid first name of person
	 */
	public boolean isValidFirstName(String name) {
		boolean isValid = false;
		String expression = "[a-zA-Z]+(')*[a-zA-Z]+";

		isValid = matchPattern(expression, name);

		return isValid;
	}

	/**
	 * @param name
	 *            Person's last name
	 * @return boolean Flag indicating if it's a valid last name
	 */
	public boolean isValidLastName(String name) {
		boolean isValid = false;
		String expression = "[a-zA-Z]+(')*[a-zA-Z]+";

		isValid = matchPattern(expression, name);

		return isValid;
	}

	/**
	 * @param uri
	 *            URI/URL
	 * @return boolean Flag indicating if it's valid URI / URL
	 */
	public boolean isValidURI(String uri) {
		boolean isValid = false;
		String expression = "^(http|https|ftp)\\:\\/\\/(((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])|([a-zA-Z0-9_\\-\\.])+\\.(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum|uk|me))((:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*)$";

		isValid = matchPattern(expression, uri);

		return isValid;
	}

	/**
	 * @param amount
	 *            Amount number
	 * @return boolean Flag indicating if it's valid amount in dollar
	 */
	public boolean isValidAmountInDollar(String amount) {
		boolean isValid = false;
		String expression = "^(\\$)?(([1-9]\\d{0,2}(\\,\\d{3})*)|([1-9]\\d*)|(0))(\\.\\d{2})?$";

		isValid = matchPattern(expression, amount);

		return isValid;
	}

	/**
	 * @param fileName
	 *            File name
	 * @return boolean Flag indicating if it's valid file name
	 */
	public boolean isValidFileName(String fileName) {
		boolean isValid = false;
		String expression = "([a-zA-Z0-9]*\\.)[a-z]{1,4}$";

		isValid = matchPattern(expression, fileName);

		return isValid;
	}

	/**
	 * @param fileName
	 *            File name
	 * @param fileExtension
	 *            file extension that needs to be validated in the given file
	 *            name
	 * @return boolean Flag indicating if it's a valid file name along with it's
	 *         provided extension.
	 */
	public boolean isValidFileName(String fileName, String fileExtension) {
		boolean isValid = false;
		String expression = "([a-zA-Z0-9]*\\.)" + fileExtension + "$";

		isValid = matchPattern(expression, fileName);

		return isValid;
	}

	/**
	 * @param s
	 *            Input string
	 * @return boolean Flag indicating if it's valid alpha numeric word
	 */
	public boolean isValidAlphaNumericWord(String s) {
		boolean isValid = false;
		String expression = "[A-Za-z0-9]+";

		isValid = matchPattern(expression, s);

		return isValid;
	}

	/**
	 * @param c
	 *            Input character
	 * @return boolean Flag indicating if it's valid alpha numeric character
	 */
	public boolean isValidAlphaNumericCharacter(Character c) {
		boolean isValid = false;
		String expression = "[A-Za-z0-9]";

		isValid = matchPattern(expression, c.toString());

		return isValid;
	}

	/**
	 * @param s
	 *            Input string
	 * @return boolean Flag indicating if it's valid alphabetic word
	 */
	public boolean isAlphabeticWords(String s) {
		boolean isValid = false;
		String expression = "[a-zA-Z]+";

		isValid = matchPattern(expression, s);

		return isValid;
	}

	/**
	 * @param c
	 *            Input character
	 * @return boolean Flag indicating if it's valid alphabetic character
	 */
	public boolean isAlphabeticCharacter(Character c) {
		boolean isValid = false;
		String expression = "[a-zA-Z]";

		isValid = matchPattern(expression, c.toString());

		return isValid;
	}

	/**
	 * @param number
	 *            Input number
	 * @return boolean Flag indicating if it's valid hexadecimal number
	 */
	public boolean isValidHexadecimalNumber(int number) {
		boolean isValid = false;
		String expression = "[A-Fa-f0-9]+";

		isValid = matchPattern(expression, String.valueOf(number));

		return isValid;
	}

	/**
	 * @param number
	 *            Input number
	 * @return boolean Flag indicating if it's valid octal number
	 */
	public boolean isValidOctalNumber(int number) {
		boolean isValid = false;
		String expression = "[0-7]+";

		isValid = matchPattern(expression, String.valueOf(number));

		return isValid;
	}

	/**
	 * @param number
	 *            Input number
	 * @return boolean Flag indicating if it's valid integer number
	 */
	public boolean isValidInteger(String number) {
		boolean isValid = false;
		String expression = "^([0-9]+)([0-9,]*)";

		isValid = matchPattern(expression, number);

		return isValid;
	}

	/**
	 * @param number
	 *            Input number
	 * @return boolean Flag indicating if it's valid float number
	 */
	public boolean isValidFloat(String number) {
		boolean isValid = false;
		String expression = "^([0-9]+)([\\d,]*).([0-9]+)";

		isValid = matchPattern(expression, number);

		return isValid;
	}

	/**
	 * @param s
	 *            Input string
	 * @return boolean Flag indicating if it's valid string
	 */
	public boolean isValidString(String s) {
		boolean isValid = false;
		String expression = "^[^<>`~!/@\\#}$%:;)(_^{&*=|'+]+$";

		isValid = matchPattern(expression, s);

		return isValid;
	}

	/**
	 * @param date
	 *            Input date
	 * @return boolean Flag indicating if date is in a valid format
	 */
	public boolean isValidDate(String date) {
		boolean isValid = false;
		String expression = "^([\\d]{4})([-:/])((0?[1-9])|((1)([0-2])))([-:/])((0?[1-9])|((1)[0-9])|((2)[0-9])|((3)[0-1]))$";

		isValid = matchPattern(expression, date);

		return isValid;
	}

	private boolean matchPattern(String expression, String stringPattern) {
		boolean isValid = false;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(stringPattern);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}

	/**
	 * �ж��ֻ������Ƿ�Ϸ�
	 * 
	 * @param phoneNumber
	 *            ��Ҫ�жϵ��ֻ�����
	 * @return ���������ֻ������ǺϷ��ģ��򷵻� true, ���򷵻� false
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		// ���������ֻ�����Ϊ�գ��򷵻� false
		if (phoneNumber == null)
			return false;
		// ����ֻ����볤��Ϊ 0���򷵻� false
		phoneNumber = phoneNumber.trim();
		if (phoneNumber.length() == 0)
			return false;

		boolean isValid = false;

		CharSequence inputStr = phoneNumber;

		Pattern pattern = Pattern.compile(REGEXP_PHONE_NUMBER);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args)
	// {
	// RegexUtil instance = new RegexUtil();

	// String email = "alex.bach@g.com";
	//
	// System.out.println("Email valid : " + instance.isValidEmail(email));
	//
	// String mobilePhone = "976-690-8150";
	// System.out.println("Mobile Valid : " +
	// instance.isValidMobilePhone(mobilePhone));
	//
	// String name = "Rupesh";
	// System.out.println("Valid Name : " + instance.isValidFirstName(name));
	//
	// String lastName = "Chavan";
	// System.out.println("Valid Last Name : " +
	// instance.isValidLastName(lastName));
	//
	// String uri = "http://255.255.255.255";
	// System.out.println("URI : " + instance.isValidURI(uri));

	// String fileName = "file.jpeg";
	// System.out.println("FileName : " + instance.isValidFileName(fileName));
	//
	// fileName = "file.jpeg";
	// System.out.println("FileName : " + instance.isValidFileName(fileName,
	// "png"));

	// String string = "Hello";
	// System.out.println("Alphanumeric String : " +
	// instance.isValidAlphaNumericWord(string));

	// Character c = 'R';
	// System.out.println("Alphanumeric Character : " +
	// instance.isValidAlphaNumericCharacter(c));

	// String s = "sdfdsfsdssd";
	// System.out.println("alphabetic characters : " +
	// instance.isAlphabeticWords(s));

	// Character c = 's';
	// System.out.println("Alphabetic character : " +
	// instance.isAlphabeticCharacter(c));

	// int hexNumber = 0xAA;
	// System.out.println("Hex number : " +
	// instance.isValidHexadecimalNumber(hexNumber));

	// int octalNUmber = 07;
	// System.out.println("Octal number : " +
	// instance.isValidOctalNumber(octalNUmber));

	// Integer number = 102;
	// System.out.println("Integer number : " +
	// instance.isValidInteger(number.toString()));

	// Float number = .01f;
	// System.out.println("Float number : " +
	// instance.isValidFloat("02,34,432.01"));

	// String s = "This is just a test.";
	// System.out.println("String : " + instance.isValidString(s));

	// String date = "2013-2-31";
	// System.out.println("Date : " + instance.isValidDate(date));
	// }
}
