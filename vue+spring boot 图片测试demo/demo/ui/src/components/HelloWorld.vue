<template>

  <div>
    <el-row>
      <el-col :span="3">
        <!-- 添加c盘下的1.jpg 2.jpg 3.jpg文件 -->
        <!-- <el-button type="primary" @click="addAll">添加图片</el-button> -->
        <el-upload class='upload-demo' multiple accept='.jpg' ref='upload' :action='updateurl' :on-success='handleSuccess' :auto-upload='true' :show-file-list='false'>
          <el-button type='primary' size="small">添加图片</el-button>
        </el-upload>
        <br>
      </el-col>
      <el-col :span="3">
        <!-- 下载数据库中所有的图片到本地D盘下 -->
        <el-button type="success" size="small" @click="downloadAll">下载图片至D:/images</el-button>
      </el-col>
      <el-col :span="3">
        <!-- 下载数据库中所有的图片到压缩文件images.zip -->
        <el-button type="warning" size="small" @click="downloadRemote">下载图片压缩至images.zip</el-button>
      </el-col>
      <el-col :span="3">
        <!-- 删除数据库中所有的图片 -->
        <el-button type="danger" size="small" @click="deleteAll">删除图片</el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-carousel :interval="4000" type="card" height="400px">
        <el-carousel-item v-for="item in imgs" :key="item">
          <img :src="item">
        </el-carousel-item>
      </el-carousel>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'HelloWorld',
  mounted: function() {
    // 页面首次加载的时候查询到文件
    this.getImages();
  },
  data() {
    return {
      updateurl: '/test/updateurl',
      imgs: []
    };
  },
  methods: {
    handleSuccess: function() {
      this.getImages();
    },
    addAll: function() {
      axios
        .post('/test/addAll')
        .then(Response => {
          this.$alert(Response.data, '', {
            confirmButtonText: '确定',
            type: 'success'
          }).then(() => {
            this.getImages();
          });
        })
        .catch(() => {
          this.$alert('添加失败', '', {
            confirmButtonText: '确定',
            type: 'error'
          });
        });
    },
    downloadAll: function() {
      axios
        .get('/test/downloadAll')
        .then(Response => {
          this.$alert(Response.data, '', {
            confirmButtonText: '确定',
            type: 'success'
          }).then(() => {
            this.getImages();
          });
        })
        .catch(() => {
          this.$alert('下载失败', '', {
            confirmButtonText: '确定',
            type: 'error'
          });
        });
    },
    downloadRemote: function() {
      window.open('/test/downloadRemote');
    },
    deleteAll: function() {
      axios
        .delete('/test/deleteAll')
        .then(Response => {
          this.$alert(Response.data, '', {
            confirmButtonText: '确定',
            type: 'success'
          }).then(() => {
            this.getImages();
          });
        })
        .catch(() => {
          this.$alert('删除失败', '', {
            confirmButtonText: '确定',
            type: 'error'
          });
        });
    },
    // 查询图片的数量信息
    getImages: function() {
      axios
        .get('/test/getImage')
        .then(Response => {
          if (
            Response.data !== null &&
            Response.data !== undefined &&
            Response.data !== ''
          ) {
            this.imgs = Response.data;
          }
        })
        .catch(() => {
          this.$alert('查询失败', '', {
            confirmButtonText: '确定',
            type: 'error'
          });
        });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
}
.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}
.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>
