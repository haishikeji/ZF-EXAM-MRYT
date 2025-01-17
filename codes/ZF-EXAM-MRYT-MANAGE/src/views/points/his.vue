<template>
  <div class="page">
    <div class="main">
      <div class="list">
        <header>
          <div class="title">答题记录</div>
          <div class="date">
            <th_icon
              @click.native="setMonth(-1)"
              class="th_icon"
              :class="date == 1 && 'active'"
            />
            <span>{{ date }}月</span>
            <th_icon_r
              @click.native="setMonth(1)"
              class="th_icon"
              :class="date == 12 && 'active'"
            />
          </div>
        </header>
        <div class="ul">
          <ul>
            <li>
              <div class="subtitle">序号</div>
              <div class="subtitle">题目</div>
              <div class="subtitle">答题日期</div>
              <div class="subtitle">答题结果</div>
            </li>
            <li v-for="(item, index) in resInfo" :key="index">
              <div class="num">{{ index + 1 }}</div>
              <div @click="goHis(item.id)" class="title">{{ item.title }}</div>
              <div class="date">{{ item.date }}</div>
              <div :class="item.res == '错误' && 'error'" class="result">
                {{ item.res }}
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { answerLog } from "@/api/point";
import th_icon from "./th_icon.vue";
import th_icon_r from "./th_r_icon.vue";
export default {
  name: "his",
  components: { th_icon, th_icon_r },
  data() {
    return {
      phone: "",
      pwd: "",
      date: new Date().getMonth() + 1,
      resInfo: "",
      userId: "",
      name: "",
    };
  },
  mounted() {
    let query = this.$route.query;
    this.userId = query.id;
    this.name = query.name;
    this.phone = query.phone;
    this.getHis(this.date + "");
  },
  methods: {
    getHis(data) {
      let m =
        new Date().getFullYear() +
        "-" +
        (this.date > 9 ? this.date : "0" + this.date);
      answerLog({ userId: this.userId, month: m }).then((res) => {
        if (res.code == 200) {
          this.resInfo = res.data;
        }
      });
    },
    setMonth(num) {
      console.log(num);
      if (num > 0) {
        if (this.date == 12) return;
        this.date + num > 12 ? (this.date = 12) : (this.date += num);
      } else {
        if (this.date == 1) return;
        this.date + num < 1 ? (this.date = 1) : (this.date += num);
      }
      this.getHis(this.date + "");
    },
    goHis(id) {
      this.$router.push(
        "/points/his_d?id=" + "&phone=" + this.phone + "&name=" + this.name
      );
    },
  },
};
</script>
<style scoped>
.page {
  box-sizing: border-box;
  overflow: hidden;
  min-width: 1024px;
}

.main {
  padding-top: 110px;
  width: 1024px;
  min-height: 680px;
  margin: 20px auto;
  border-radius: 12px;
  box-sizing: border-box;
  background: #5db0f2;
  overflow: scroll;
}

.list {
  width: 728px;
  min-height: 536px;
  margin: auto;
  border-radius: 16px;
  background: #fffae8;
  border: 12px solid #ff8b2d;
}

header {
  display: flex;
  justify-content: space-between;
  width: 664px;
  height: 52px;
  font-weight: 600;
  margin: 32px auto 20px;
  border-bottom: 1px solid rgba(188, 188, 188, 0.4);
}

header .title {
  color: #db4518;
}

.date {
  display: flex;
  user-select: none;
}

.th_icon {
  color: #3d3d3d;
  font-size: 20px;
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.th_icon.active {
  color: #bcbcbc;
}

span {
  margin: 0 12px;
}

.ul {
  width: 664px;
  margin: auto;
}

li {
  font-weight: 600;
  color: #3d3d3d;
  font-size: 14px;
  text-align: center;
  display: flex;
  align-items: center;
  height: 36px;
  box-sizing: border-box;
}

li:nth-child(n) div {
  background: rgba(219, 69, 24, 0.06);
}

li:nth-child(2n) div {
  background: rgba(219, 69, 24, 0.02);
}

li div {
  width: 125px;
  height: 100%;
  margin-left: 1px;
  line-height: 36px;
}

li div:nth-child(1) {
  width: 68px;
  flex-shrink: 1;
  margin-left: 0;
}

li div:nth-child(2) {
  flex: 1;
  padding: 0 20px;
  box-sizing: border-box;
}

li div:nth-child(3),
li div:nth-child(4) {
  width: 120px;
  flex-shrink: 1;
}

li:nth-child(1) {
  color: white;
}

li:nth-child(1) div {
  background: #db4518;
}

li .title {
  cursor: pointer;
  text-decoration: underline;
  word-wrap: break-word;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

li .result.error {
  color: #db4518;
}
</style>
