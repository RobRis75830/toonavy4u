package entities;

public class Pagination {

        /** how many series lists per page  **/
        private int pageSize = 10;

        /** rangesize for each page **/
        private int rangeSize = 10;

        /** current page**/
        private int curPage = 1;

        /** current range **/
        private int curRange = 1;

        /** total lists **/
        private int listCnt;

        /** total page number **/
        private int pageCnt;

        /** total range number **/
        private int rangeCnt;

        /** starting page **/
        private int startPage = 1;

        /** end page **/
        private int endPage = 1;

        /** starting index **/
        private int startIndex = 0;

        /** previous page **/
        private int prevPage;

        /** next page **/
        private int nextPage;


}
