package com.zeei.das.cgs.T212;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cgs.vo.FormulaVo;

import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.tokenizer.ParseException;

/**
 * @类型名称：FormulaComputing @类型描述：
 * @功能描述：公式计算方法
 * @创建作者：zhanghu
 *
 */
public class FormulaComputing {

	// 需要公式计算的数据 类型
	static String[] cnArray = { "2011", "2031", "2051", "2061" };

	private static Logger logger = LoggerFactory.getLogger(FormulaComputing.class);

	/**
	 * dataParser:对数据参照站点配置公式 进行解析，获取新的因子数据 void
	 * 
	 * @throws ParseException
	 */
	public static JSONArray dataParser(String CN, List<FormulaVo> formulas, JSONArray params) throws ParseException {

		// CN为需要计算公式中的，则进行计算
		if (ArrayUtils.contains(cnArray, CN)) {

			// 结果值Map
			JSONObject result = new JSONObject();
			// 数据格式转换
			JSONObject paramsMap = dataChange(params);

			JSONArray rets = new JSONArray();

			// 新的数据集合
			JSONObject resultMap = new JSONObject();

			for (FormulaVo formula : formulas) {

				String polluteCode = formula.getPolluteCode();

				// 公式内容
				String formulaStr = formula.getFormulaStr();
				// 公式内容不能为空的
				if (StringUtils.isEmpty(formulaStr)) {
					continue;
				}
				// 遍历变量并从CP的params中获取对应的值
				boolean returnFlag = true;
				String min = scopearser(formulaStr, paramsMap, "Min");
				String rtd = scopearser(formulaStr, paramsMap, "Rtd");
				String avg = scopearser(formulaStr, paramsMap, "Avg");
				String max = scopearser(formulaStr, paramsMap, "Max");

				result = new JSONObject();
				result.put("ParamID", formula.getPolluteCode());
				result.put("Cou", "0.000");
				result.put("Flag", "N");
				// 结果集存入map。
				resultMap.put(polluteCode, result);

				if (StringUtils.isNotEmpty(min)) {
					result.put("Min", min);
				}
				if (StringUtils.isNotEmpty(rtd)) {
					result.put("Rtd", rtd);
					returnFlag = false;
				}
				if (StringUtils.isNotEmpty(avg)) {
					result.put("Avg", avg);
					returnFlag = false;
				}
				if (StringUtils.isNotEmpty(max)) {
					result.put("Max", max);
				}
				if (returnFlag) {
					logger.error(String.format("站点:%s-%s 公式(%s)配置错误，部分因子值为空无法计算%s", formula.getPointCode(),
							formula.getPolluteCode(), formulaStr, params.toString()));
					continue;
				}
			}
			// 将新的因子数据合并入老的数据集，若新因子名称和老因子相同，则会覆盖老的数据值
			paramsMap.putAll(resultMap);
			rets.addAll(paramsMap.values());
			return rets;
		}
		return params;
	}

	/**
	 * scopearser:公式计算对应的值
	 * 
	 * @param formulaStr
	 * @param paramsMap
	 * @param valName
	 * @return
	 * @throws ParseException
	 *             String
	 */
	private static String scopearser(String formulaStr, JSONObject paramsMap, String valName) throws ParseException {

		Scope scope = new Scope();
		Expression expr = Parser.parse(formulaStr, scope);

		// 获取到公式中的所有变量名
		Set<String> scoNames = scope.getLocalNames();
		// 遍历变量并从CP的params中获取对应的值
		for (String pollute : scoNames) {
			if (!paramsMap.containsKey(pollute)) {
				return "";
			}
			JSONObject pollValue = paramsMap.getJSONObject(pollute);
			String value = pollValue.getString(valName);
			if (StringUtils.isBlank(value)) {
				return "";
			}
			scope.getVariable(pollute).withValue(Double.valueOf(value));
		}
		String result = String.valueOf(expr.evaluate());
		// 表达式计算产生异常时，结果值为Infinity
		if ("Infinity".equals(result)) {
			String error = String.format("%s公式计算异常(如：除数为零)！(计算%s)参数集[%s]", formulaStr, valName, paramsMap.toString());
			logger.error(error);
			return "";
		}

		if (isNumeric(result)) {
			return result;
		} else {
			return "";
		}

	}

	/**
	 * dataChange:转换
	 * 
	 * @return Map<String,Map<String,String>>
	 */
	private static JSONObject dataChange(JSONArray params) {

		JSONObject result = new JSONObject();
		for (Object p : params) {
			JSONObject param = (JSONObject) p;
			result.put(param.getString("ParamID"), param);
		}
		return result;

	}

	/**
	 * isNumeric: 校验是否是数字
	 * 
	 * @param str
	 * @return boolean
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws ParseException {

		Scope scope = new Scope();
		Expression expr = Parser
				.parse("1*1.414*0.84*sqrt((if(a83022<0,0,a83022))/((30*(101325))/(8312*(273+a01012))))*9*3600", scope);
		// 39.5*1.414*0.84*sqrt(a83022/((30*a01013)/(8312*(273+a01012))))
		//Set<String> scoNames = scope.getLocalNames();
		//System.out.println(JSON.toJSON(scoNames));
		//scope.getVariable("a01012").withValue(40);
		//scope.getVariable("a01013").withValue(103063.95);
		// scope.getVariable("a83022").withValue(-1);
		String result = String.valueOf(expr.evaluate());

		System.out.println(result);

		/*
		 * a83022 差压 40.765 a01013 烟气压力 a01012 烟气温度 52.4
		 */
	}

}
