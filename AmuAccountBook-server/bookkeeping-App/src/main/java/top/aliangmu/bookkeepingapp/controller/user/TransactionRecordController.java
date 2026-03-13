package top.aliangmu.bookkeepingapp.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aliangmu.bookkeepingapp.domain.dto.AddTransactionRecordDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateTransactionRecordDTO;
import top.aliangmu.bookkeepingapp.domain.vo.AddTransactionRecordVO;
import top.aliangmu.bookkeepingapp.domain.vo.PageResponseVO;
import top.aliangmu.bookkeepingapp.domain.vo.TransactionRecordVO;
import top.aliangmu.bookkeepingapp.domain.vo.TransactionStatsVO;
import top.aliangmu.bookkeepingapp.response.Result;
import top.aliangmu.bookkeepingapp.service.TransactionRecordService;

import java.util.List;

@RestController
@RequestMapping("/user/transaction-record")
@Tag(name = "交易记录相关")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionRecordController {

    private final TransactionRecordService transactionRecordService;

    @PostMapping("/add")
    @Operation(summary = "新增交易流水")
    public Result<AddTransactionRecordVO> add(@RequestBody AddTransactionRecordDTO dto) {
        AddTransactionRecordVO vo = transactionRecordService.add(dto);
        return Result.data(vo);
    }

    @PutMapping("/update")
    @Operation(summary = "更新交易流水数据")
    public Result<Void> update(@RequestBody UpdateTransactionRecordDTO dto) {
        transactionRecordService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/one/{id}")
    @Operation(summary = "删除交易流水数据")
    public Result<Void> delete(@PathVariable Long id) {
        transactionRecordService.delete(id);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "加载更多信息")
    public Result<PageResponseVO<TransactionRecordVO>> loadMore(@RequestParam Integer page,
                                                                @RequestParam Integer pageSize) {
        PageResponseVO<TransactionRecordVO> vo = transactionRecordService.loadMore(page, pageSize);
        return Result.data(vo);
    }

    @DeleteMapping("/all/{userId}")
    @Operation(summary = "删除所以的交易记录")
    public Result<Void> clearAll(@PathVariable Long userId) {
        transactionRecordService.clearAll(userId);
        return Result.success();
    }

    @GetMapping("/stats/month")
    @Operation(summary = "获取年份里指定月份所以消费记录")
    public Result<List<TransactionRecordVO> > getMonthRecord(@RequestParam Integer year,
                                                      @RequestParam Integer month) {
        List<TransactionRecordVO>  vo = transactionRecordService.getMonthRecord(year, month);
        return Result.data(vo);
    }


    @GetMapping("/stats/{year}")
    @Operation(summary = "获取年份里所有月份的消费统计")
    public Result<?> getStats(@PathVariable Integer year) {
        TransactionStatsVO vo = transactionRecordService.getStats(year);
        return Result.data(vo);
    }
}
