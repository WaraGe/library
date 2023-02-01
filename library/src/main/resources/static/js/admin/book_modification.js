window.onload = () => {
  BookModificationService.getInstance().setBookCode();
  BookModificationService.getInstance().loadCategories();
  BookModificationService.getInstance().loadBookAndImageData();

  ComponentEvent.getInstance().addClickEventModificationButton();
  ComponentEvent.getInstance().addClickEventImgAddButton();
  ComponentEvent.getInstance().addChangeEventImgFile();
  ComponentEvent.getInstance().addClickEventImgModificationButton();
  ComponentEvent.getInstance().addClickEventImgCancelButton();
};
const bookObj = {
  bookCode: "",
  bookName: "",
  author: "",
  publisher: "",
  publicationDate: "",
  category: "",
};

const imgObj = {
  imageId: null,
  bookCode: null,
  saveName: null,
  originName: null,
};

const fileObj = {
  files: new Array(),
  formData: new FormData(),
};

class BookModificationApi {
  static #instance = null;
  static getInstance() {
    if (this.#instance == null) {
      this.#instance = new BookModificationApi();
    }
    return this.#instance;
  }

  getBookAndImage() {
    let responseData = null;

    $.ajax({
      async: false,
      type: "get",
      url: `http://127.0.0.1:8000/api/admin/book/${bookObj.bookCode}`,
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

  getCategories() {
    let responseData = null;

    $.ajax({
      async: false,
      type: "get",
      url: "http://127.0.0.1:8000/api/admin/categories",
      dateType: "json",
      success: (response) => {
        responseData = response.data;
      },
      error: (error) => {
        console.log(error);
      },
    });
    return responseData;
  }
  modifyBook() {
    let successFlag = false;

    $.ajax({
      async: false,
      type: "put",
      url: `http://127.0.0.1:8000/api/admin/book/${bookObj.bookCode}`,
      contentType: "application/json",
      data: JSON.stringify(bookObj),
      dataType: "json",
      success: (response) => {
        successFlag = true;
        // console.log(response);
      },
      error: (error) => {
        console.log(error);
        BookModificationService.getInstance().setErrors(
          error.responseJSON.data
        );
      },
    });

    return successFlag;
  }

  removeImg() {
    let successFlag = false;
    $.ajax({
      async: false,
      type: "delete",
      url: `http://127.0.0.1:8000/api/admin/book/${bookObj.bookCode}/image/${imgObj.imageId}`,
      dataType: "json",
      success: (response) => {
        successFlag = true;
      },
      error: (error) => {
        console.log(error);
      },
    });
    return successFlag;
  }

  registerImg() {
    $.ajax({
      async: false,
      type: "post",
      url: `http://127.0.0.1:8000/api/admin/book/${bookObj.bookCode}/images`,
      encType: "multipart/form-data", //json형식이랑 비슷함 multipart로 전달할때 밑에 있는 두줄은 세트
      contentType: false,
      processData: false,
      data: fileObj.formData,
      dataType: "json",
      success: (response) => {
        //이미지 등록 성공
        alert("도서 이미지가 성공적으로 수정되었습니다");
        location.reload();
      },
      error: (error) => {
        console.log(error);
      },
    });
  }
}

class BookModificationService {
  static #instance = null;
  static getInstance() {
    if (this.#instance == null) {
      this.#instance = new BookModificationService();
    }
    return this.#instance;
  }

  setBookCode() {
    const URLSearch = new URLSearchParams(location.search); // 경로 표출
    // URLSearchParams를 통해서 객체를 불러온다면 한글경로가
    bookObj.bookCode = URLSearch.get("bookCode");
    // console.log(URLSearch.get("bookCode"));
  }

  setBookObjValues() {
    const modificationInputs = document.querySelectorAll(".modification-input");

    bookObj.bookCode = modificationInputs[0].value;
    bookObj.bookName = modificationInputs[1].value;
    bookObj.author = modificationInputs[2].value;
    bookObj.publisher = modificationInputs[3].value;
    bookObj.publicationDate = modificationInputs[4].value;
    bookObj.category = modificationInputs[5].value;
  }

  loadBookAndImageData() {
    const responseData = BookModificationApi.getInstance().getBookAndImage();

    if (responseData.bookMst == null) {
      alert("해당 도서코드는 존재하지 않습니다");
      history.back(); // 뒤로가기
      return;
    }
    const modificationInput = document.querySelectorAll(".modification-input");
    modificationInput[0].value = responseData.bookMst.bookCode;
    modificationInput[1].value = responseData.bookMst.bookName;
    modificationInput[2].value = responseData.bookMst.author;
    modificationInput[3].value = responseData.bookMst.publisher;
    modificationInput[4].value = responseData.bookMst.publicationDate;
    modificationInput[5].value = responseData.bookMst.category;

    if (responseData.bookImage != null) {
      imgObj.imageId = responseData.bookImage.imageId;
      imgObj.bookCode = responseData.bookImage.bookCode;
      imgObj.saveName = responseData.bookImage.saveName;
      imgObj.originName = responseData.bookImage.originName;

      const bookImg = document.querySelector(".book-img");
      bookImg.src = `http://127.0.0.1:8000/image/book/${responseData.bookImage.saveName}`;
    }
  }

  loadCategories() {
    const responseData = BookModificationApi.getInstance().getCategories();

    const categorySelect = document.querySelector(".category-select");
    categorySelect.innerHTML = `<option value="">전체조회</option>`;

    responseData.forEach((data) => {
      categorySelect.innerHTML += `
        <option value="${data.category}">${data.category}</option>
      `;
    });
  }
  setErrors(errors) {
    const errorMessages = document.querySelectorAll(".error-message");
    this.clearErrors();

    Object.keys(errors).forEach((key) => {
      if (key == "bookCode") {
        errorMessages[0].innerHTML = errors[key];
      } else if (key == "bookName") {
        errorMessages[1].innerHTML = errors[key];
      } else if (key == "category") {
        errorMessages[5].innerHTML = errors[key];
      }
    });
  }
  clearErrors() {
    const errorMessages = document.querySelectorAll(".error-message");
    errorMessages.forEach((error) => {
      console.log(error);
      error.innerHTML = "";
    });
  }
}

class ImgFileService {
  static #instance = null;
  static getInstance() {
    if (this.#instance == null) {
      this.#instance = new ImgFileService();
    }
    return this.#instance;
  }
  getImgPreview() {
    const bookImg = document.querySelector(".book-img");

    const reader = new FileReader(); // js내장객체 = 파일을 읽어주는 객체 (1 => 파일을 읽고)

    reader.onload = (e) => {
      // (3)
      bookImg.src = e.target.result; //src에 이미지 경로를 넣는 작업 html의 src 경로를 바꾸므로 파일을 변경
    };

    reader.readAsDataURL(fileObj.files[0]); // 배열의 첫번째값 (이미지객체) (2 => readAsDataURL의 값을 받아서 윗줄의 onload를 실행)
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

  addClickEventModificationButton() {
    const modificationButton = document.querySelector(".modification-button");

    modificationButton.onclick = () => {
      //비어 있는값에 버튼을 눌렸을때 요청을 실행
      BookModificationService.getInstance().setBookObjValues();
      const successFlag = BookModificationApi.getInstance().modifyBook();

      if (!successFlag) {
        return;
      }

      BookModificationService.getInstance().clearErrors();

      // 임시로 disabled를 풀기
      if (confirm("도서 이미지를 수정하시겠습니까?")) {
        const imgAddButton = document.querySelector(".img-add-button");
        const imgCancelButton = document.querySelector(".img-cancel-button");

        imgAddButton.disabled = false; // yes를 눌렸을시 실행 => disable 무력화
        imgCancelButton.disabled = false; // yes를 눌렸을시 실행 => disable 무력화
      } else {
        // no를 눌렸을 시 실행
        location.reload(); //새로고침 실행
      }
    };
  }

  addClickEventImgAddButton() {
    const imgFile = document.querySelector(".img-file");
    const addButton = document.querySelector(".img-add-button");

    addButton.onclick = () => {
      imgFile.click();
    };
  }
  addChangeEventImgFile() {
    const imgFile = document.querySelector(".img-file");
    imgFile.onchange = () => {
      const formData = new FormData(document.querySelector(".img-form"));
      // form안에 있는 모든 input들 name을 기준으로 key값을 가지고 있음
      // 선택을하면 그 선택된 값이 value가 되어 값들을 꺼낼수가 있음
      // = 클릭을 한다면 선택기가 나오고 열기가 된다면 파일 형식을 표현해야 하므로 change이벤트로 변경을 하는것.

      let changeFlag = false; // 이미지 선택시 같은 파일을 열기를 하면 바뀐것이 아니므로 false값

      fileObj.files.pop(); // 배열 형식으로 +를 눌릴때마다 배열이 늘어나기때문에 change를 할때마다 array값을 비우는것 pop(); => 1장의 사진의 값만 받아올것 이기때문

      formData.forEach((value) => {
        console.log(value);

        if (value.size != 0) {
          // 파일의 사이즈가 0인지 아닌지 확인
          fileObj.files.push(value);
          changeFlag = true;
        }
      });
      if (changeFlag) {
        const imgModificationButton = document.querySelector(
          ".img-modification-button"
        );
        imgModificationButton.disabled = false; // 이미지등로 버튼 풀림

        ImgFileService.getInstance().getImgPreview(); // 이미지가 바꼈을때
        imgFile.value = null; //file의 값을 비워줌
      }
    };
  }

  addClickEventImgModificationButton() {
    const imgModificationButton = document.querySelector(
      ".img-modification-button"
    );

    imgModificationButton.onclick = () => {
      fileObj.formData.append("files", fileObj.files[0]); // formData = Map이랑 비슷함 k,v값이 들어감

      let successFlag = true;

      if(imgObj.imageId != null) {
        successFlag = BookModificationApi.getInstance().removeImg(); // 삭제가 성공적으로 되었을때만 true로 넘어감
      }
      if(successFlag) {
        BookModificationApi.getInstance().registerImg();
      }
      
    };
  }

  addClickEventImgCancelButton() {
    const imgCancelButton = document.querySelector(".img-cancel-button");

    imgCancelButton.onclick = () => {
      if (confirm("정말로 이미지 수정을 취소하시겠습니까?")) {
        location.reload();
      }
    };
  }
}
