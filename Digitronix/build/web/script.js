// Price Range Filter Live Update
document.addEventListener('DOMContentLoaded', function () {
  const priceMin = document.getElementById('priceMin');
  const priceMax = document.getElementById('priceMax');
  const priceRangeLabel = document.getElementById('priceRangeLabel');
  if (priceMin && priceMax && priceRangeLabel) {
    function updatePriceLabel() {
      let min = Math.min(Number(priceMin.value), Number(priceMax.value));
      let max = Math.max(Number(priceMin.value), Number(priceMax.value));
      priceRangeLabel.textContent = `Price: Rs. ${min.toLocaleString()} – Rs. ${max.toLocaleString()}`;
    }
    priceMin.addEventListener('input', updatePriceLabel);
    priceMax.addEventListener('input', updatePriceLabel);
    updatePriceLabel();
  }
});
// Price Range Filter Logic
const priceMin = document.getElementById('priceMin');
const priceMax = document.getElementById('priceMax');
const priceRangeLabel = document.getElementById('priceRangeLabel');
if (priceMin && priceMax && priceRangeLabel) {
  function updatePriceLabel() {
    let min = Math.min(Number(priceMin.value), Number(priceMax.value));
    let max = Math.max(Number(priceMin.value), Number(priceMax.value));
    priceRangeLabel.textContent = `Price: Rs. ${min.toLocaleString()} – Rs. ${max.toLocaleString()}`;
  }
  priceMin.addEventListener('input', updatePriceLabel);
  priceMax.addEventListener('input', updatePriceLabel);
  updatePriceLabel();
}
document.getElementById('applyPriceFilter')?.addEventListener('click', function () {
  // Implement filter logic here
  alert(priceRangeLabel.textContent + ' (filter logic not implemented)');
});
// Add shadow to category bar on scroll
window.addEventListener('scroll', function () {
  var categoryBar = document.getElementById('category-bar');
  if (window.scrollY > 10) {
    categoryBar.classList.add('scrolled');
  } else {
    categoryBar.classList.remove('scrolled');
  }
});


// Add shadow to category bar on scroll
window.addEventListener('scroll', function () {
  var categoryBar = document.getElementById('category-bar');
  if (window.scrollY > 10) {
    categoryBar.classList.add('scrolled');
  } else {
    categoryBar.classList.remove('scrolled');
  }
});


function loadDonutChart() {
  const data = {
    labels: ['Mobile', 'Tabs', 'Speakers'],
    datasets: [{
      label: 'Total Revenue',
      data: [300, 450, 150],
      backgroundColor: [
        'rgb(255, 99, 132)',
        'rgb(54, 162, 235)',
        'rgb(255, 205, 86)'
      ],
      hoverOffset: 4
    }]
  };

  const config = {
    type: 'doughnut',
    data: data,
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    }
  };

  const ctx = document.getElementById('donutChart').getContext('2d');
  new Chart(ctx, config);
}


function loadLineChart() {
  const labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  const data = {
    labels: labels,
    datasets: [{
      label: 'Sales',
      data: [30, 60, 50, 70, 50, 90, 30],
      fill: false,
      borderColor: 'rgba(255, 255, 255, 1)',  // white line
      borderWidth: 2,
      tension: 0.4, // for smooth curves
      pointRadius: 0  // hide points
    }]
  };

  const config = {
    type: 'line',
    data: data,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false // hides "Sales" label
        },
        tooltip: {
          enabled: false // hides tooltips
        }
      },
      scales: {
        x: {
          display: false // hides x-axis
        },
        y: {
          display: false // hides y-axis
        }
      }
    }
  };

  const ctx = document.getElementById('lineChart').getContext('2d');
  new Chart(ctx, config);
}
