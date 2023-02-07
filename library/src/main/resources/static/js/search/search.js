window.onload = () => {
  HeaderService.getInstance().loadHeader();

  SearchService.getInstance().clearBookList();
  SearchService.getInstance().loadSearchBooks();
  SearchService.getInstance().loadCategories();
  SearchService.getInstance().setMaxPage();
  
  ComponentEvent.getInstance().addClickEventCategoryCheckboxs(); 
  ComponentEvent.getInstance().addScrollEventPaging(); 
  ComponentEvent.getInstance().addClickEventSearchButton(); 
};

let MaxPage = 0

const searchObj = {
  page: 1,
  searchValue: null,
  categories: new Array(), //하나만 검색하면 안되기때문!
  count: 10
};

class SearchApi {
  static #instance = null;
  static getInstance() {
    if (this.#instance == null) {
      this.#instance = new SearchApi();
    }
    return this.#instance;
  }
  getCategories() {
    let returnData = null;

    $.ajax({
      async: false,
      type: "get",
      url: "http://127.0.0.1:8000/api/admin/categories",
      dataType: "json",
      success: (response) => {
        console.log(response);
        returnData = response.data;
      },
      error: (error) => {
        console.log(error);
      },
    });

    return returnData;
  }
  getTotalCount() {
    let responseData = null;

    $.ajax({
      async: false,
      type: "get",
      url: "http://127.0.0.1:8000/api/search/totalcount",
      data: searchObj,
      dataType: "json",
      success: (response) => {
        responseData = response.data;
        console.log(`검색한 갯수: ${responseData}`);
      },
      error: (error) => {
        console.log(error);
      },
    });
    return responseData;
  }

  searchBook() {
    let responseData = null;

    $.ajax({
      async: false,
      type: "get",
      url: "http://127.0.0.1:8000/api/search",
      data: searchObj,
      dataType: "json",
      success: (response) => {
        responseData = response.data;
      },
      error: (error) => {
        console.log(error);
      },
    });
    return responseData;
  }
}

class SearchService {
  static #instance = null;
  static getInstance() {
    if (this.#instance == null) {
      this.#instance = new SearchService();
    }
    return this.#instance;
  }

  setMaxPage() {
    const totalcount = SearchApi.getInstance().getTotalCount();
    MaxPage = totalcount % 10 == 0 
    ? totalcount / 10 
    : Math.floor(totalcount / 10) + 1;
  }

  loadCategories() {
    const categoryList = document.querySelector(".category-list");
    categoryList.innerHTML = ``;

    const reponseData = SearchApi.getInstance().getCategories();
    reponseData.forEach(categoryObj => {
      categoryList.innerHTML += `
        <div class="category-item">
          <input type="checkbox" class="category-checkbox" id="${categoryObj.category}" value="${categoryObj.category}">
          <label for="${categoryObj.category}">${categoryObj.category}</label>
        </div>
      `;
    })
  }

// 검색이 된다던지, 페이지 새로고침시 실행
  clearBookList() {
    const contentFlex = document.querySelector(".content-flex");
    contentFlex.innerHTML = "";
  }
  
  loadSearchBooks() {
    const responseData = SearchApi.getInstance().searchBook();
    const contentFlex = document.querySelector(".content-flex");

    responseData.forEach(data => {
      contentFlex.innerHTML += `
        <div class="info-container">
              <div class="book-desc">
                <div class="img-container">
                  <img src="http://127.0.0.1:8000/image/book/${data.saveName != null ? data.saveName : "noimg.gif"}" class="book-img">
                </div>
                <div class="like-info"><i class="fa-regular fa-thumbs-up"></i><span class="like-count">${data.likeCount != null ? data.likeCount : 0}</span></div>
              </div>
              
              <div class="book-info">
                <div class="book-code">${data.bookCode}</div>
                <h3 class="book-name">${data.bookName}</h3>
                <div class="info-text book-author"><b>저자: </b>${data.author}</div>
                <div class="info-text book-publisher"><b>출판사: </b>${data.publisher}</div>
                <div class="info-text book-publicationdate"><b>출판일: </b>${data.publicationDate}</div>
                <div class="info-text book-category"><b>카테고리: </b>${data.category}</div>
                <div class="book-buttons">
                  <button type="button" class="rental-button">대여하기</button>
                  <button type="button" class="like-button">쪼아요하기</button>
                </div>
              </div>
            </div>
        </div>
      `;
    })
  }
}

class ComponentEvent {
  static #instance = null;
  static getInstance() {
    if (this.#instance == null) {
      this.#instance = new ComponentEvent();
    }
    return this.#instance;
  }

  addClickEventCategoryCheckboxs() {
    const checkboxs = document.querySelectorAll(".category-checkbox");

    checkboxs.forEach(checkbox => {
      checkbox.onclick = () => {
        if (checkbox.checked) {
          searchObj.categories.push(checkbox.value);
        } else {
          const index = searchObj.categories.indexOf(checkbox.value);
          searchObj.categories.splice(index, 1);
        }
        console.log(searchObj.categories);
        document.querySelector(".search-button").onclick(); //카테고리 선정시 자동으로 선택
      };
    });
  }
  addScrollEventPaging() {
    const html = document.querySelector("html");
    const body = document.querySelector("body");
    

    body.onscroll = () => {
      const scrollPosition = body.offsetHeight - html.clientHeight - html.scrollTop;
      console.log("높이: " + scrollPosition);
      
      if(scrollPosition < 250 && searchObj.page < MaxPage) {
        searchObj.page++;
        SearchService.getInstance().loadSearchBooks();
      }
    }
  }
  
  addClickEventSearchButton() {
    const searchButton = document.querySelector(".search-button");
    const searchInput = document.querySelector(".search-input");
    searchButton.onclick = () => {
      searchObj.searchValue = searchInput.value;
      searchObj.page = 1; // 1page부터 정렬
      window.scrollTo(0, 0); //최상단으로 이동
      SearchService.getInstance().clearBookList(); // 기존의 데이터 삭제
      SearchService.getInstance().setMaxPage(); // 검색 결과에 대한 최대 페이지
      SearchService.getInstance().loadSearchBooks(); // 카테고리 검색결과 가져오기
    }

    searchInput.onkeyup = () => {
      if(window.event.keyCode == 13) { // enter 클릭
        searchButton.click();
      }
    }
  }
}
