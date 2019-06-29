package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author :动力节点张开
 * 2019-5-27
 */
@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {
    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Double queryHistoryAverageRate() {
//        设置序列化方式
        redisTemplate.setStringSerializer(new StringRedisSerializer());
//        从redis获取记录
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
//        判断缓存是否为空
        if (historyAverageRate==null){
            historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

            redisTemplate.opsForValue().set("historyAverageRate", historyAverageRate,15, TimeUnit.MINUTES);

        }

        return historyAverageRate;
    }

    /**
     * 根据类型获得产品，不适合放入缓存，因为有个剩余可投额，需要实时更新
     * @param mapParam 3个参数，产品类型，页码，显示多少条数据
     * @return
     */
    @Override
    public List<LoanInfo> queryProductListByType(Map<String, Object> mapParam) {

        /*List<LoanInfo>  loanInfoList = (List<LoanInfo>) redisTemplate.opsForHash().get("product", mapParam.get("productType"));
        if (null == loanInfoList) {

            loanInfoList =   loanInfoMapper.selectProductListByType(mapParam);
            redisTemplate.opsForHash().put("product", mapParam.get("productType"), loanInfoList);
        }*/

        List<LoanInfo> loanInfoList =   loanInfoMapper.selectProductListByType(mapParam);
        return loanInfoList;
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();
//        获取总记录数
        Long total = loanInfoMapper.selectTotal(paramMap);
//        获取产品列表
        List<LoanInfo> loanInfoList = loanInfoMapper.selectProductListByType(paramMap);
        paginationVO.setDataList(loanInfoList);
        paginationVO.setTotal(total);
        return paginationVO;
    }


    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(id);
        return loanInfo;
    }


}
