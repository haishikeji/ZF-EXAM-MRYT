<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";

import { useRouter } from "vue-router";
import { _login, _code } from "../http/api";
import { useDailyStore } from "../stores/daily";
const isShow = ref(false);
const rt = useRouter();
const data = reactive({
  phone: "",
  pwd: "",
  nickname: "",
  img: "",
  uuid: "",
  code: "",
});
onMounted(() => {
  getCode();
  let phone = localStorage.getItem("hudp");
  let pwd = localStorage.getItem("hudpw");
  // let nickname = localStorage.getItem('hudn');
  if (phone && pwd) {
    data.phone = phone;
    data.pwd = pwd;
  }
});
// method
function getCode() {
  _code().then((res: any) => {
    data.img = "data:image/gif;base64," + res.img;
    data.uuid = res.uuid;
  });
}
function login() {
  if (!data.phone || data.phone.length != 11 || data.phone[0] != "1") {
    return window.alert("请输入正确的号码");
  }
  // if(!data.nickname)return window.alert('请输入昵称');
  if (!data.pwd) return window.alert("请输入密码");
  if (!data.code) return window.alert("请输入验证码");

  _login({ phone: data.phone, pwd: data.pwd, code: data.code }).then(
    (res: any) => {
      if (res.success) {
        // if(res.data.pwdDay>0){
        const daily = useDailyStore();
        daily.setLogin(res.data.token);
        daily.setInfo(data.phone, res.data.nickName || "");
        daily.setAdmin(res.data.isDeptAdmin);
        localStorage.setItem("hudp", data.phone);
        localStorage.setItem("hudpw", data.pwd);
        // localStorage.setItem('hudn',data.nickname);
        if (res.data.id > 0) {
          rt.push("/his_d?id=" + res.data.id);
        } else {
          rt.push("/daily");
        }
        // }else{
        //     alert('密码每90天内必须修改一次，请先修改密码')
        // }
      }
    },
    (error) => {
      console.log(error);
      alert(error.msg);
    }
  );
}
function register() {
  rt.push("/psw?register=1");
}
function changePwd() {
  rt.push("/psw");
}
</script>

<template>
  <div class="page">
    <div class="main">
      <div class="form">
        <div class="subtitle">登录</div>
        <form action="">
          <div class="input">
            <label for="phone">手机号码</label
            ><input
              id="phone"
              type="text"
              oninput="value=value.replace(/[\u4E00-\u9FA5]/g,'')"
              v-model="data.phone"
              placeholder="请输入手机号码"
              maxlength="11"
            />
          </div>
          <!-- <div class="input">
                        <label for="nickName">用户昵称</label><input autocomplete="off" id="nickName" type="text" max="20"  v-model="data.nickname" placeholder="请输入用户昵称" maxlength="11">
                    </div> -->

          <div class="input">
            <label for="password">密码</label
            ><input
              :type="isShow ? 'text' : 'password'"
              id="password"
              placeholder="请输入密码"
              v-model="data.pwd"
              maxlength="20"
            />
            <img
              v-if="isShow"
              @click="isShow = !isShow"
              src="../assets/psd.png"
              alt="i"
            />
            <img
              v-else
              @click="isShow = !isShow"
              src="../assets/psw.png"
              alt="i"
            />
          </div>
          <div class="input">
            <label for="code">验证码</label
            ><input
              autocomplete="off"
              id="code"
              type="tel"
              v-model="data.code"
              oninput="value=value.replace(/[\u4E00-\u9FA5]/g,'')"
              placeholder="请输入验证码"
              maxlength="11"
            />
            <img
              v-if="data.img"
              @click="getCode"
              :src="data.img"
              class="code"
              alt="code"
            />
          </div>
          <div @click="login" class="btn cursor">登录</div>
        </form>
        <div class="action">
          <span @click="register">帐号注册</span>
          <span @click="changePwd">修改密码</span>
        </div>
        <p>使用说明</p>
        <p>1．每天回答一道题目，答对一题积一分，答错可重答一次</p>
        <p>2．每年以覆盖率、平均分等为依据纳入党组织党建考核</p>
        <p>3．答题活动实行常态化管理，不再进行月度、年度奖励</p>
      </div>
    </div>
  </div>
</template>
<style lang="less" scoped>
input {
  -webkit-autofill: initial;
  -moz-autofill: initial;
  -ms-autofill: initial;
  -webkit-text-fill-color: currentColor !important;
}
input::placeholder {
  color: gray;
}
.page {
  width: 100vw;
  box-sizing: border-box;
  overflow: hidden;
  min-width: 1024px;

  .main {
    padding-top: 100px;
    // width: 1024px;
    // height: 680px;
    // margin: 44px auto;
    border-radius: 12px;
    box-sizing: border-box;
    // background: #5db0f2;

    .form {
      width: 500px;
      height: 570px;
      border-radius: 16px;
      padding: 30px;
      opacity: 1;
      margin: auto;
      background: #fffae8;
      box-sizing: border-box;
      border: 12px solid #5db0f2;
      .subtitle {
        font-size: 20px;
        color: #3d3d3d;
        text-align: center;
        font-weight: 600;
        margin-bottom: 20px;
      }
      .input {
        margin-top: 12px;
        padding-right: 20px;
        width: 400px;
        height: 52px;
        border-radius: 12px;
        background: white;
        display: flex;
        align-items: center;
        color: #3d3d3d;
        font-size: 14px;
        label {
          width: 110px;
          padding: 0 20px;
          box-sizing: border-box;
          flex-shrink: 1;
          text-align: left;
        }
        input {
          flex: 1;
          color: #3d3d3d;
        }
        img {
          width: 16px;
          height: 16px;
          cursor: pointer;
        }
        .code {
          width: 115.56px;
          height: 40px;
        }
      }
      .btn {
        width: 300px;
        height: 56px;
        border-radius: 12px;
        font-size: 24px;
        margin: 20px auto;
        background: #5db0f2;
        text-align: center;
        line-height: 56px;
        color: white;
      }
      .action {
        display: flex;
        align-items: center;

        justify-content: center;
        margin-bottom: 60px;
        span {
          padding: 0 20px;
          text-decoration: underline;
          cursor: pointer;
        }
      }
      p {
        font-size: 14px;
        color: #6d6d6d;
        &:first-of-type {
          margin-top: 60px;
          color: black;
          font-weight: 600;
          margin-bottom: 12px;
        }
      }
    }
  }
}
</style>
