<template>
  <div class="page">
    <div class="main">
      <div class="content">
        <div class="l"></div>
        <div class="bg"></div>
        <div class="question_title">
          <div class="title">历史答题</div>
          <div class="subtitle">答题时间 {{ questionInfo.date }}</div>
        </div>
        <div v-if="questionInfo && questionInfo.id" class="question">
          <div class="txt">{{ questionInfo.title }}</div>

          <div
            v-for="(item, index) in questionInfo.options"
            :key="index"
            class="answer"
          >
            <input
              v-if="
                (questionInfo && questionInfo.type == 1) ||
                (questionInfo && questionInfo.type == 3)
              "
              :disabled="true"
              :checked="
                questionInfo.uoptions
                  ? questionInfo.uoptions.indexOf(item.name) > -1
                  : false
              "
              name="answer"
              type="radio"
              class="cursor"
            />

            <input
              v-if="questionInfo && questionInfo.type == 2"
              name="answer"
              :disabled="true"
              type="checkbox"
              :checked="
                questionInfo.uoptions
                  ? questionInfo.uoptions.indexOf(item.name) > -1
                  : false
              "
              class="cursor"
            />
            <label>{{ item.name + " " + item.value }}</label>
          </div>
        </div>

        <div class="answer_his">
          <p
            :class="
              questionInfo.coptions == questionInfo.uoptions
                ? 'success'
                : 'error'
            "
          >
            {{
              questionInfo.coptions == questionInfo.uoptions
                ? "回答正确"
                : "回答错误"
            }}
          </p>
          <p>正确答案：</p>
          <p>{{ questionInfo.coptions }}</p>
        </div>
        <div v-if="questionInfo && questionInfo.id" class="action">
          <div @click="gohis" class="his cursor">返回</div>
        </div>
      </div>
      <div v-if="pontInfo" class="total">
        <div class="month">
          <p>本月统计</p>
          <div>
            <p>积分</p>
            <span>{{ pontInfo.monthPoints }}</span>
            <p>错题</p>
            <span>{{ pontInfo.monthWrongNum }}</span>
          </div>
        </div>
        <div class="year">
          <p>本年统计</p>
          <div>
            <p>积分</p>
            <span>{{ pontInfo.yearPoints }}</span>
            <p>错题</p>
            <span>{{ pontInfo.yearWrongNum }}</span>
          </div>
        </div>
      </div>

      <div v-if="rankArr" class="rank">
        <div class="title">排行榜</div>
        <div class="rank_list">
          <div class="month">
            <ul>
              <li>
                <div class="subtitle">排名</div>
                <div class="subtitle">月积分</div>
                <div class="subtitle">人数</div>
              </li>
              <li v-for="(item, index) in rankArr.months" :key="index">
                <div>{{ index + 1 }}</div>
                <div>{{ item.points }}</div>
                <div>{{ item.num }}</div>
              </li>
            </ul>
          </div>

          <div class="year">
            <ul>
              <li>
                <div class="subtitle">排名</div>
                <div class="subtitle">年积分</div>
                <div class="subtitle">人数</div>
              </li>
              <li v-for="(item, index) in rankArr.years" :key="index">
                <div>{{ index + 1 }}</div>
                <div>{{ item.points }}</div>
                <div>{{ item.num }}</div>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="bottom"></div>
    </div>
  </div>
</template>
<script>
import { _question, _point, _rank } from "@/api/point";
export default {
  name: "his_d",
  data() {
    return {
      phone: "",
      pwd: "",
      nickName: "",
      questionInfo: "",
      pontInfo: "",
      rankArr: "",
      id: "",
    };
  },
  mounted() {
    let query = this.$route.query;
    this.id = query.id;
    this.nickName = query.name;
    this.phone = query.phone;
    _question(this.id).then((res) => {
      if (res.success) {
        this.questionInfo = res.data;
      }
    });
    _point(this.phone).then((res) => {
      if (res.success) {
        this.pontInfo = res.data;
      }
    });
    _rank().then((res) => {
      if (res.success) {
        this.rankArr = res.data;
      }
    });
  },
  methods: {
    setOptions(inx, name) {
      // if(this.questionInfo.type=='1'){
      //     if(opt.value[inx])return opt.value[inx]='';
      //     opt.value[inx] = name;
      // }else{
      //     opt.value[0] = name
      // }
    },
    gohis() {
      this.$router.back();
    },
  },
};
</script>
<style scoped>
.page {
  box-sizing: border-box;
  min-width: 1024px;
  padding: 40px 40px 0;
}

.page .main {
  padding-top: 98px;
  width: 1024px;
  min-height: 680px;
  margin: 44px auto;
  border-radius: 12px;
  box-sizing: border-box;
  background: #5db0f2;
  overflow: scroll;
}

.page .main .content {
  width: 728px;
  min-height: 362px;
  position: relative;
  border-radius: 16px;
  margin: auto;
  padding: 32px;
  box-sizing: border-box;
  background: #fffae8;
  border: 12px solid #ff8b2d;
}
.page .main .content .l {
  position: absolute;
  top: -100px;
  left: -130px;
  width: 380px;
  height: 80px;
  z-index: 9;
}
.page .main .content .bg {
  position: absolute;
  top: -38px;
  right: 44px;
  width: 250px;
  height: 76px;
  z-index: 9;
  background: url("../../assets/d.jpg") left top/ 100% 100% no-repeat;
}

.page .main .content .question_title {
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(188, 188, 188, 0.4);
}

.page .main .content .question_title .title {
  font-size: 20px;
  line-height: 32px;
  font-weight: 600;
  color: #db4518;
}

.page .main .content .question_title .subtitle {
  font-size: 14px;
  color: #3d3d3d;
  margin-top: 4px;
}

.page .main .content .question {
  margin-top: 20px;
}

.page .main .content .question .txt {
  font-size: 16px;
  font-weight: 600;
  line-height: 23px;
  margin-bottom: 8px;
}

.page .main .content .question .answer {
  margin-top: 8px;
  font-size: 16px;
  line-height: 24px;
}

.page .main .content .answer_his {
  display: flex;
  margin-top: 16px;
}

.page .main .content .answer_his p {
  font-size: 16px;
  color: #3d3d3d;
  line-height: 23px;

  &.success {
    color: #db4518;
  }

  &.error {
    color: #f78f2d;
  }

  &:nth-child(1) {
    font-weight: 600;
    margin-right: 16px;
  }
}

.page .main .content .action {
  margin-top: 20px;
  display: flex;
}

.page .main .content .action .sub {
  width: 76px;
  height: 36px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  line-height: 36px;
  color: white;
  background: #db4518;
}

.page .main .content .action .his {
  width: 104px;
  height: 36px;
  border-radius: 8px;
  color: #db4518;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  box-sizing: border-box;
  line-height: 36px;
  border: 1px solid #db4518;
}

.page .main .total {
  width: 752px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 16px auto;
}

.page .main .total .month,
.page .main .total .year {
  width: 368px;
  height: 150px;
  border-radius: 16px;
  background: #fffae8;
  border: 12px solid #ff8b2d;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 32px;
}

.page .main .total .month,
.page .main .total .year p {
  font-size: 20px;
  line-height: 32px;
  font-weight: 600;
  color: #db4518;
}

.page .main .total .month div,
.page .main .total .year div {
  display: flex;
  font-size: 20px;
  line-height: 32px;
  font-weight: 600;
  margin-top: 8;
}

.page .main .total .month div p,
.page .main .total .year div p {
  color: #3d3d3d;
}

.page .main .total .month div p:nth-of-type(2),
.page .main .total .year div p:nth-of-type(2) {
  margin-left: 20px;
}

.page .main .total .month div span,
.page .main .total .year div span {
  margin-left: 4px;
  color: #db4518;
}

.page .main .bottom {
  height: 40px;
}

.page .rank {
  width: 752px;
  border-radius: 16px;
  padding: 32px;
  margin: auto;
  box-sizing: border-box;
  border: 12px solid #ff8b2d;
  background: #fffae8;
}

.page .rank .title {
  height: 49px;
  width: 100%;
  font-size: 20px;
  color: #db4518;
  line-height: 1;
  font-weight: 600;
  border-bottom: 1px solid rgba(188, 188, 188, 0.4);
}

.page .rank .rank_list {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 20px;
}
p,
ul {
  margin: 0;
  padding: 0;
}

.page .rank .rank_list .month ul li,
.page .rank .rank_list .year ul li {
  font-weight: 600;
  color: #3d3d3d;
  font-size: 14px;
  padding: 8px 0;
  text-align: center;
  display: flex;
  align-items: center;
  height: 36px;
}

.page .rank .rank_list .month ul li:nth-child(n),
.page .rank .rank_list .year ul li:nth-child(n) {
  background: rgba(219, 69, 24, 0.06);
}

.page .rank .rank_list .month ul li:nth-child(2n),
.page .rank .rank_list .year ul li:nth-child(2n) {
  background: rgba(219, 69, 24, 0.02);
}

.page .rank .rank_list .month ul li:nth-child(1),
.page .rank .rank_list .year ul li:nth-child(1) {
  background: #db4518;
  color: white;
}

.page .rank .rank_list .month ul li div,
.page .rank .rank_list .year ul li div {
  width: 125px;
}

.page .rank .rank_list .month ul li div:nth-child(1),
.page .rank .rank_list .year ul li div:nth-child(1) {
  width: 68px;
}
</style>
