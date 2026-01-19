# 分页响应格式

## PageResult 类定义

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "分页响应")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> content;
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "当前页码（从 1 开始）", example = "1")
    private Integer page;
    
    @Schema(description = "每页数量", example = "10")
    private Integer size;
    
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;
    
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    @Schema(description = "是否为第一页", example = "true")
    private Boolean isFirst;
    
    @Schema(description = "是否为最后一页", example = "false")
    private Boolean isLast;
    
    /**
     * 从 Spring Data Page 对象构建
     */
    public static <T> PageResult<T> of(Page<T> page, Integer pageNumber) {
        return PageResult.<T>builder()
                .content(page.getContent())
                .total(page.getTotalElements())
                .page(pageNumber)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
    
    /**
     * 从列表和总数构建
     */
    public static <T> PageResult<T> of(List<T> content, Long total, 
                                       Integer page, Integer size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return PageResult.<T>builder()
                .content(content)
                .total(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .hasPrevious(page > 1)
                .hasNext(page < totalPages)
                .isFirst(page == 1)
                .isLast(page >= totalPages)
                .build();
    }
    
    /**
     * 空分页结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return PageResult.<T>builder()
                .content(Collections.emptyList())
                .total(0L)
                .page(page)
                .size(size)
                .totalPages(0)
                .hasPrevious(false)
                .hasNext(false)
                .isFirst(true)
                .isLast(true)
                .build();
    }
}
```
