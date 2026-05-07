/**
 * 无人机管理系统 — 企业级公共 JS 工具库 v3.0
 * 提供 Toast、Loading、Modal、API、表单验证、用户菜单等通用功能
 */
;(function(global) {
  'use strict';

  // ==================== Toast ====================
  var Toast = {
    _ctr: null,
    _getCtr: function() {
      if (!this._ctr) {
        this._ctr = document.createElement('div');
        this._ctr.className = 'toast-container';
        document.body.appendChild(this._ctr);
      }
      return this._ctr;
    },
    _icon: {success:'\u2713',error:'\u2715',warning:'\u26A0',info:'\u2139'},
    show: function(msg, type, duration) {
      type = type || 'info';
      duration = duration || 3000;
      var el = document.createElement('div');
      el.className = 'toast toast-' + type;
      el.innerHTML = '<span class="toast-icon">' + (this._icon[type] || '') + '</span><span>' + esc(msg) + '</span>';
      this._getCtr().appendChild(el);
      setTimeout(function() {
        el.style.opacity = '0';
        el.style.transition = 'opacity 0.3s';
        setTimeout(function() { el.parentNode && el.parentNode.removeChild(el); }, 300);
      }, duration);
    },
    success: function(m) { this.show(m, 'success'); },
    error: function(m) { this.show(m, 'error', 5000); },
    warning: function(m) { this.show(m, 'warning', 4000); },
    info: function(m) { this.show(m, 'info'); }
  };

  // ==================== Loading ====================
  var Loading = {
    _el: null,
    _get: function() {
      if (!this._el) {
        this._el = document.createElement('div');
        this._el.className = 'loading-mask';
        this._el.innerHTML = '<div class="loading-spinner"></div><div class="loading-text">处理中...</div>';
        document.body.appendChild(this._el);
      }
      return this._el;
    },
    show: function(txt) {
      var el = this._get();
      if (txt) el.querySelector('.loading-text').textContent = txt;
      el.classList.add('show');
    },
    hide: function() { this._get().classList.remove('show'); }
  };

  // ==================== Modal ====================
  var Modal = {
    confirm: function(opts) {
      opts = Object.assign({
        title: '\u64CD\u4F5C\u786E\u8BA4',
        message: '\u786E\u5B9A\u8981\u6267\u884C\u6B64\u64CD\u4F5C\u5417\uFF1F',
        okText: '\u786E\u5B9A',
        cancelText: '\u53D6\u6D88',
        type: 'warning',
        onOk: function() {},
        onCancel: function() {}
      }, opts);

      var colorMap = {warning: '#e6a23c', danger: '#f56c6c', info: '#409eff'};
      var color = colorMap[opts.type] || colorMap.warning;
      var btnClass = opts.type === 'danger' ? 'btn-danger' : 'btn-primary';

      var overlay = document.createElement('div');
      overlay.className = 'modal-overlay';
      overlay.innerHTML =
        '<div class="modal-box">' +
        '<div class="modal-hd" style="color:' + color + '"><span>\u26A0</span><span>' + esc(opts.title) + '</span></div>' +
        '<div class="modal-bd">' + opts.message + '</div>' +
        '<div class="modal-ft">' +
        '<button class="btn btn-default modal-cancel">' + esc(opts.cancelText) + '</button>' +
        '<button class="btn ' + btnClass + ' modal-ok">' + esc(opts.okText) + '</button>' +
        '</div></div>';
      document.body.appendChild(overlay);

      var close = function() {
        document.body.removeChild(overlay);
      };
      overlay.querySelector('.modal-cancel').onclick = function() { close(); opts.onCancel(); };
      overlay.querySelector('.modal-ok').onclick = function() { close(); opts.onOk(); };
      overlay.addEventListener('click', function(e) {
        if (e.target === overlay) { close(); opts.onCancel(); }
      });
    }
  };

  // ==================== API ====================
  var Api = {
    request: function(method, url, data) {
      var headers = { 'Content-Type': 'application/json' };
      var token = localStorage.getItem('accessToken');
      if (token && token !== 'null' && token !== 'undefined') headers['Authorization'] = 'Bearer ' + token;
      return fetch(url, {
        method: method,
        headers: headers,
        body: data ? JSON.stringify(data) : undefined
      }).then(function(resp) {
        if (resp.status === 401) {
          var refreshToken = localStorage.getItem('refreshToken');
          if (refreshToken) {
            return fetch('/api/v1/auth/refresh', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ refreshToken: refreshToken })
            }).then(function(r) { return r.json(); }).then(function(body) {
              if (body.code === 200 && body.data) {
                localStorage.setItem('accessToken', body.data.accessToken);
                localStorage.setItem('refreshToken', body.data.refreshToken);
                headers['Authorization'] = 'Bearer ' + body.data.accessToken;
                return fetch(url, { method: method, headers: headers, body: data ? JSON.stringify(data) : undefined });
              }
              localStorage.clear();
              window.location.href = '/login';
              throw new Error('登录已过期，请重新登录');
            });
          }
          localStorage.clear();
          window.location.href = '/login';
          throw new Error('登录已过期，请重新登录');
        }
        if (resp.status === 403) {
          throw new Error('权限不足，无法执行此操作');
        }
        return resp.json().then(function(body) {
          if (!resp.ok) {
            var e = new Error(body.message || '\u8BF7\u6C42\u5931\u8D25 (' + resp.status + ')');
            e.status = resp.status;
            e.body = body;
            throw e;
          }
          return body;
        });
      });
    },
    get: function(u) { return this.request('GET', u); },
    post: function(u, d) { return this.request('POST', u, d); },
    put: function(u, d) { return this.request('PUT', u, d); },
    del: function(u) { return this.request('DELETE', u); }
  };

  // ==================== 删除无人机 ====================
  function deleteDrone(id, sn) {
    Modal.confirm({
      title: '\u5220\u9664\u65E0\u4EBA\u673A',
      message: '\u786E\u5B9A\u8981\u5220\u9664\u65E0\u4EBA\u673A <strong>"' + esc(sn) + '"</strong> \u5417\uFF1F<br><span style="color:#f56c6c;font-size:12px;">\u6B64\u64CD\u4F5C\u4E0D\u53EF\u6062\u590D\uFF01</span>',
      okText: '\u786E\u8BA4\u5220\u9664',
      type: 'danger',
      onOk: function() {
        Loading.show('\u6B63\u5728\u5220\u9664...');
        Api.del('/api/v1/drones/' + id).then(function() {
          Loading.hide();
          Toast.success('\u5220\u9664\u6210\u529F');
          setTimeout(function() { location.reload(); }, 800);
        }).catch(function(e) {
          Loading.hide();
          Toast.error('\u5220\u9664\u5931\u8D25: ' + e.message);
        });
      }
    });
  }

  // ==================== URL 参数解析 ====================
  function getParams() {
    var p = {};
    var s = location.search.substring(1);
    if (!s) return p;
    s.split('&').forEach(function(kv) {
      var a = kv.split('=');
      if (a[0]) p[decodeURIComponent(a[0])] = decodeURIComponent(a[1] || '');
    });
    return p;
  }

  // ==================== 表单脏检测 ====================
  function formDirty(formSel) {
    var form = document.querySelector(formSel);
    if (!form) return;
    var dirty = false;
    var orig = new FormData(form).toString();
    form.addEventListener('input', function() { dirty = (new FormData(form).toString() !== orig); });
    form.addEventListener('change', function() { dirty = (new FormData(form).toString() !== orig); });
    window.addEventListener('beforeunload', function(e) {
      if (dirty) {
        e.preventDefault();
        e.returnValue = '\u60A8\u6709\u672A\u4FDD\u5B58\u7684\u66F4\u6539\uFF0C\u786E\u5B9A\u8981\u79BB\u5F00\u5417\uFF1F';
        return e.returnValue;
      }
    });
    return {
      isDirty: function() { return dirty; },
      reset: function() { dirty = false; orig = new FormData(form).toString(); }
    };
  }

  // ==================== 工具函数 ====================
  function esc(s) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(s || ''));
    return d.innerHTML;
  }

  function $(s) { return document.querySelector(s); }
  function $$(s) { return document.querySelectorAll(s); }

  // ==================== JWT 过期检查 ====================
  function isTokenExpired(token) {
    if (!token || token === 'null' || token === 'undefined') return true;
    try {
      var payload = JSON.parse(atob(token.split('.')[1]));
      return !payload.exp || (payload.exp * 1000) < Date.now();
    } catch (e) {
      return true;
    }
  }

  // ==================== Auth Guard ====================
  ;(function checkAuth() {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', checkAuth);
      return;
    }
    var path = window.location.pathname;
    var isProtected = /^\/(drones|dashboard|missions|flight-logs|maintenance)(\/|$)/.test(path);
    var token = localStorage.getItem('accessToken');
    if (isProtected && isTokenExpired(token)) {
      localStorage.clear();
      window.location.href = '/login';
    }
  })();

  // ==================== 用户头像菜单 ====================
  ;(function initUserMenu() {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', initUserMenu);
      return;
    }
    var path = window.location.pathname;
    var isAppPage = /^\/(drones|dashboard|missions|flight-logs|maintenance)(\/|$)/.test(path);
    if (!isAppPage) return;

    var token = localStorage.getItem('accessToken');
    if (!token || token === 'null' || token === 'undefined') return;

    var userInfo = {};
    try { userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}'); } catch(e) {}
    var displayName = userInfo.nickname || userInfo.email || '用户';

    var headerRight = document.querySelector('.app-header-right');
    if (!headerRight) {
      var appHeader = document.querySelector('.app-header');
      if (appHeader) {
        headerRight = document.createElement('div');
        headerRight.className = 'app-header-right';
        appHeader.appendChild(headerRight);
      } else {
        var wrapper = document.querySelector('.app-wrapper');
        if (!wrapper) return;
        appHeader = document.createElement('div');
        appHeader.className = 'app-header';
        appHeader.innerHTML = '<div class="app-header-left"></div><div class="app-header-right"></div>';
        var navTabs = wrapper.querySelector('.nav-tabs');
        if (navTabs) {
          navTabs.insertAdjacentElement('afterend', appHeader);
        } else {
          wrapper.insertAdjacentElement('afterbegin', appHeader);
        }
        headerRight = appHeader.querySelector('.app-header-right');
      }
    }

    var svgIcon = '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="8" r="4" fill="#909399"/><path d="M4 20c0-3.3 3.6-6 8-6s8 2.7 8 6" stroke="#909399" stroke-width="2" fill="none" stroke-linecap="round"/></svg>';

    var menuHtml =
      '<div class="user-menu-wrapper">' +
        '<div class="user-avatar" id="userAvatarBtn">' +
          '<span class="avatar-icon">' + svgIcon + '</span>' +
        '</div>' +
        '<div class="user-dropdown" id="userDropdown">' +
          '<div class="dropdown-header">' +
            '<span class="dropdown-avatar-icon">' + svgIcon + '</span>' +
            '<div class="dropdown-user-info">' +
              '<div class="dropdown-name">' + esc(displayName) + '</div>' +
              '<div class="dropdown-role">' + esc(userInfo.role || 'user') + '</div>' +
            '</div>' +
          '</div>' +
          '<div class="dropdown-divider"></div>' +
          '<a class="dropdown-item" id="menuLogout"><span class="dropdown-icon">&#8618;</span> 退出登录</a>' +
          '<a class="dropdown-item danger" id="menuDeleteAccount"><span class="dropdown-icon">&#128465;</span> 注销账号</a>' +
        '</div>' +
      '</div>';

    headerRight.insertAdjacentHTML('beforeend', menuHtml);

    var avatarBtn = document.getElementById('userAvatarBtn');
    var dropdown = document.getElementById('userDropdown');

    avatarBtn.addEventListener('click', function(e) {
      e.stopPropagation();
      dropdown.classList.toggle('show');
    });

    document.addEventListener('click', function(e) {
      if (!dropdown.contains(e.target) && !avatarBtn.contains(e.target)) {
        dropdown.classList.remove('show');
      }
    });

    document.getElementById('menuLogout').addEventListener('click', function() {
      dropdown.classList.remove('show');
      Modal.confirm({
        title: '退出登录',
        message: '确定要退出登录吗？',
        type: 'info',
        onOk: function() {
          var rt = localStorage.getItem('refreshToken');
          if (rt) {
            Api.post('/api/v1/auth/logout', { refreshToken: rt }).catch(function(){});
          }
          localStorage.clear();
          window.location.href = '/login';
        }
      });
    });

    document.getElementById('menuDeleteAccount').addEventListener('click', function() {
      dropdown.classList.remove('show');
      Modal.confirm({
        title: '注销账号',
        message: '<strong style="color:#f56c6c">此操作不可逆！</strong><br>注销后您的所有数据将被永久删除，确定要继续吗？',
        okText: '确认注销',
        type: 'danger',
        onOk: function() {
          var email = userInfo.email;
          if (!email) { Toast.error('无法获取用户信息'); return; }
          Toast.info('正在发送验证码...');
          Api.post('/api/v1/auth/send-verify-code', { email: email }).then(function(res) {
             var hint = '验证码已发送至 ' + email;
              if (res.data && res.data.code) { hint += '（测试模式验证码：' + res.data.code + '）'; }
            var code = prompt(hint + '，请输入验证码：');
            if (!code) return;
            Api.post('/api/v1/auth/delete-account', { code: code }).then(function() {
              Toast.success('账号已注销');
              localStorage.clear();
              setTimeout(function() { window.location.href = '/login'; }, 1500);
            }).catch(function(e) { Toast.error('注销失败: ' + e.message); });
          }).catch(function(e) { Toast.error('验证码发送失败: ' + e.message); });
        }
      });
    });
  })();

  // ==================== 暴露全局 ====================
  global.Toast = Toast;
  global.Loading = Loading;
  global.Modal = Modal;
  global.Api = Api;
  global.deleteDrone = deleteDrone;
  global.getParams = getParams;
  global.formDirty = formDirty;
  global.esc = esc;
  global.$ = $;
  global.$$ = $$;
  global.isTokenExpired = isTokenExpired;

})(window);
