window.onload = () => {
  BookService.getInstance().loadBookList();
  BookService.getInstance().loadCategories();
  BookService.getInstance().loadSearchNumberList();
  ComponentEvent.getInstance().addClickEventSearchButton();
}

let searchObj = {
  page : 5,
  category : "",
  searchValue : "",
  order : "bookId",
  limit : "Y",
  count : 20
};

class BookSearchApi {
  static #instance = null;
  static getInstance() {
      if(this.#instance == null) {
          this.#instance = new BookSearchApi();
      }
      return this.#instance;
  }

  getBookList(searchObj) {
      let returnData = null;

      $.ajax({
          async: false,
          type: "get",
          url: "http://127.0.0.1:8000/api/admin/books",
          data: searchObj,
          dataType: "json",
          success: response => {
              console.log(response);
              returnData = response.data;
          },
          error: error => {
              console.log(error);
          }
      });

      return returnData;
  }
  getCategories() {
    let returnData = null;

      $.ajax({
        async: false,
        type: "get",
        url: "http://127.0.0.1:8000/api/admin/categories",
        dataType: "json",
        success: response => {
            console.log(response);
            returnData = response.data;
        },
        error: error => {
            console.log(error);
        }
    });

    return returnData;
  }

  getBookTotalCount(searchObj) {
    let returnData = null;

    $.ajax({
        async: false,
        type: "get",
        url: "http://127.0.0.1:8000/api/admin/books/totalcount",
        data: {
          "category" : searchObj.category,
          "searchValue": searchObj.searchValue
        },
        dataType: "json",
        success: response => {
            console.log(response);
            returnData = response.data;
        },
        error: error => {
            console.log(error);
        }
    });

    return returnData;
}

}


class BookService {
  static #instance = null;
  static getInstance() {
      if(this.#instance == null) {
          this.#instance = new BookService();
      }
      return this.#instance;
  }

  loadBookList() {
      const responseData = BookSearchApi.getInstance().getBookList(searchObj);
      //tbody 객체를 들고오기
      const bookListBody = document.querySelector(".content-table tbody");
      bookListBody.innerHTML = ""; //빈값으로 비워주기

      responseData.forEach((data, index) => {
        bookListBody.innerHTML += `
        <tr>
          <td><input type="checkbox" /></td>
          <td>${data.bookId}</td>
          <td>${data.bookCode}</td>
          <td>${data.bookName}</td>
          <td>${data.author}</td>
          <td>${data.publisher}</td>
          <td>${data.publicationDate}</td>
          <td>${data.category}</td>
          <td>${data.rentalStatus == "Y" ? "대여중" : "대여가능"}</td>
          <td><i class="fa-solid fa-square-pen"></i></td>
        </tr>
        `;
      });
      this.loadSearchNumberList();
    }

    loadSearchNumberList() {
      const pageController = document.querySelector(".page-controller");
      pageController.innerHTML = "";

      const totalCount = BookSearchApi.getInstance().getBookTotalCount(searchObj);                      // Math.floor (값) 소수점단위를 버림
      const maxPageNumber = totalCount % searchObj.count == 0 
                        ? Math.floor(totalCount / searchObj.count) 
                        : Math.floor(totalCount / searchObj.count) + 1;

// javascript:void(0) => 클릭이벤트는 살리면서 href기능을 상실하게 하는방법!
      pageController.innerHTML = `
          <a href="javascript:void(0)" class="pre-button disabled">이전</a>
          <ul class="page-numbers">
            
          </ul>
          <a href="javascript:void(0)" class="next-button disabled">다음</a>
      `;

      if(searchObj.page != 1) { //감소
        const preButton = pageController.querySelector(".pre-button")
        preButton.classList.remove("disabled");
        
        preButton.onclick = () => {
          searchObj.page--;

          this.loadBookList();
        }
      }
      if(searchObj.page != maxPageNumber) { //증가
        const nextButton = pageController.querySelector(".next-button")
        nextButton.classList.remove("disabled");
        
        nextButton.onclick = () => {
          searchObj.page++;

          this.loadBookList();
        }
      }

      const startIndex = searchObj.page % 5 == 0 
                        ? searchObj.page - 4 
                        : searchObj.page - (searchObj.page % 5) + 1;
      const endIndex = startIndex + 4 <= maxPageNumber 
                        ? startIndex + 4
                        : maxPageNumber;
      const pageNumbers = document.querySelector('.page-numbers');
      

      for(let i = startIndex; i <= endIndex; i++) {
        pageNumbers.innerHTML += `
        <a href="javascript:void(0)"class="page-button ${i == searchObj.page ? "disabled" : ""}"><li>${i}</li></a>`
      }

      const pageButtons = document.querySelectorAll('.page-button');
      pageButtons.forEach((button) => {
        const pageNumber = button.textContent;
        if(pageNumber != searchObj.page) {
          button.onclick = () => {
            searchObj.page = pageNumber;
            this.loadBookList(); // page값을 덮어씨우고 새로 불러옴
          }
        } 
      })
    }
    

    loadCategories() {
      const responseData = BookSearchApi.getInstance().getCategories();

      const categorySelect = document.querySelector('.category-select');
      categorySelect.innerHTML = `<option value="">전체조회</option>`;

      responseData.forEach(data => {
        categorySelect.innerHTML += `
          <option value="${data.category}">${data.category}</option>
        `;
      })
    }
}

class ComponentEvent {
  static #instance = null;
  static getInstance() {
      if(this.#instance == null) {
          this.#instance = new ComponentEvent();
      }
      return this.#instance;
    }
    addClickEventSearchButton() {
      const categorySelect = document.querySelector(".category-select");
      const searchInput = document.querySelector(".search-input");
      const searchButton = document.querySelector(".search-button");

      searchButton.onclick = () => {
        searchObj.category = categorySelect.value;
        searchObj.searchValue = searchInput.value;
        searchObj.page = 1; //  검색을 한다면 1번페이지로 이동
        BookService.getInstance().loadBookList();
        console.log("검색실행");
      }

      searchInput.onkeyup = () => { 
        if(window.event.keyCode == 13) { //enter키가 들어온다면 13 == enter
          searchButton.click(); // 위의 searchButton을 클릭한 것과 동작이 같음
        }
      }
    }
}