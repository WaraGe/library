window.onload = () => {
  ComponentEvent.getInstance().addClickEventRegisterButton();
  
  ComponentEvent.getInstance().addClickEventImgAddButton();
  ComponentEvent.getInstance().addChangeEventImgFile();
};

const fileObj = {
  files: new Array()
};

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
    const reader = new FileReader(); // js내장객체 = 파일을 읽어주는 객체 (1 파일을 읽고)
    reader.onload = (e) => { // (3)
      bookImg.src = e.target.result; //src에 이미지 경로를 넣는 작업
    }
    reader.readAsDataURL(fileObj.files[0]); // 배열의 첫번째값 (이미지객체) (2 url데이터를 받아서 onread를 실행)
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
  
  addClickEventRegisterButton() {
    const registerButton =  document.querySelector(".register-button");

    registerButton.onclick = () => {
      // 임시로 disabled를 풀기
      if(confirm("도서 이미지를 등록하시겠습니까?")) {
        const imgAddButton = document.querySelector(".img-add-button");
        const imgRegisterButton = document.querySelector(".img-register-button");

        imgAddButton.disabled = false; // yes를 눌렸을시 실행 => disable 무력화
        imgRegisterButton = false; // yes를 눌렸을시 실행 => disable 무력화
      }else { // no를 눌렸을 시 실행
        location.reload(); //새로고침 실행
      }
    }
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
          fileObj.files.push(value);
          changeFlag = true;
        }
      });
      if (changeFlag) {
        console.log("A");
        ImgFileService.getInstance().getImgPreview(); // 이미지가 바꼈을때 위
        imgFile.value = null;
      }
    };
  }
}
