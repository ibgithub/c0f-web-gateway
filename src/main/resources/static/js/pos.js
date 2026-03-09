document.addEventListener("DOMContentLoaded", function () {
    let cart=[]
    document.querySelectorAll(".product-card")
        .forEach(card=>{
            card.addEventListener("click",()=>{
                card.classList.add("product-clicked")

                setTimeout(()=>{
                    card.classList.remove("product-clicked")
                },250)
                let id=card.dataset.id
                let name=card.dataset.name
                let price=parseFloat(card.dataset.price)
                let item=cart.find(i=>i.id==id)
                if(item){
                    item.qty++
                }else{
                    cart.push({id,name,price,qty:1})
                }
                renderCart()
            })
        })

    function renderCart(){

        let html=""
        let total=0

        cart.forEach(i=>{

            let subtotal=i.qty*i.price
            total+=subtotal

            html+=`
<div class="cart-row d-flex justify-content-between align-items-center mb-3"
     data-id="${i.id}">

    <div>

        <div class="fw-semibold">${i.name}</div>

        <div class="d-flex align-items-center gap-2 mt-1">

            <button class="btn btn-sm btn-outline-secondary qty-minus"
                    data-id="${i.id}">
                -
            </button>

            <span class="fw-semibold">${i.qty}</span>

            <button class="btn btn-sm btn-outline-secondary qty-plus"
                    data-id="${i.id}">
                +
            </button>

        </div>

    </div>

    <div class="d-flex align-items-center gap-2">

        <span class="fw-semibold">
            ${formatMoney(subtotal)}
        </span>

        <button class="btn btn-sm btn-outline-danger remove-item"
                data-id="${i.id}">
            ×
        </button>

    </div>

</div>
`
        scrollCartToBottom();
        })

        document.getElementById("cartItems").innerHTML=html

        document.getElementById("subtotal").innerText=formatMoney(total)
        document.getElementById("total").innerText=formatMoney(total)

        highlightLastCartItem()

    }

    document.addEventListener("click", function(e){

        if(e.target.classList.contains("remove-item")){

            let id = e.target.dataset.id

            cart = cart.filter(i => i.id != id)

            renderCart()

        }

    })
    function scrollCartToBottom(){
        let cartBox = document.getElementById("cartItems");
        if(cartBox){
            cartBox.scrollTop = cartBox.scrollHeight;
        }
    }
    function formatMoney(val){
        return "Rp "+val.toLocaleString("id-ID")
    }

    // CATEGORY FILTER (lebih stabil)
    document.addEventListener("click", function(e){

        if(e.target.classList.contains("category-btn")){

            document.querySelectorAll(".category-btn")
                .forEach(b => b.classList.remove("active"));

            e.target.classList.add("active");

            let category = e.target.dataset.category;
            let keyword = document.getElementById("productSearch").value.toLowerCase();

            filterProducts(keyword, category);

        }

    });

    // SEARCH PRODUCT
    const searchInput = document.getElementById("productSearch");

    searchInput?.addEventListener("keydown", function(e){

        if(e.key === "Enter"){

            e.preventDefault()

            let firstVisibleProduct = document.querySelector(
                ".product-card:not([style*='display: none'])"
            )

            if(firstVisibleProduct){
                firstVisibleProduct.click()
            }

        }

    })

    if(searchInput){

        searchInput.addEventListener("keyup", function(){

            let keyword = this.value.toLowerCase()
            let activeCategory =
                document.querySelector(".category-btn.active").dataset.category

            document.querySelectorAll(".product-card")
                .forEach(card=>{

                    let name = card.dataset.name.toLowerCase()
                    let category = card.dataset.category

                    let matchName = name.includes(keyword)
                    let matchCategory =
                        activeCategory === "all" || category == activeCategory

                    if(matchName && matchCategory){
                        card.style.display="block"
                    }else{
                        card.style.display="none"
                    }

                })

        })

    }

    function filterProducts(keyword, category){

        document.querySelectorAll(".product-card")
            .forEach(card => {

                let name = card.dataset.name.toLowerCase();
                let productCategory = card.dataset.category;

                let matchName = name.includes(keyword);
                let matchCategory =
                    category === "all" || productCategory == category;

                if(matchName && matchCategory){
                    card.style.display = "block";
                }else{
                    card.style.display = "none";
                }

            });

    }
    function highlightLastCartItem(){

        setTimeout(()=>{

            let rows=document.querySelectorAll(".cart-row")

            let last=rows[rows.length-1]

            if(last){
                last.classList.add("cart-highlight")
            }

        },10)

    }
    document.getElementById("clearCart")?.addEventListener("click", function(){

        if(confirm("Batalkan semua pesanan?")){

            cart = []
            renderCart()

        }

    })
    // tambah qty
    document.addEventListener("click", function(e){

        if(e.target.classList.contains("qty-plus")){

            let id = e.target.dataset.id

            let item = cart.find(i => i.id == id)

            if(item){
                item.qty++
                renderCart()

            }

        }

    })

// kurangi qty
    document.addEventListener("click", function(e){

        if(e.target.classList.contains("qty-minus")){

            let id = e.target.dataset.id

            let item = cart.find(i => i.id == id)

            if(item){

                item.qty--

                if(item.qty <= 0){
                    cart = cart.filter(i => i.id != id)
                }

                renderCart()

            }

        }

    })
    document.getElementById("productSearch")?.focus();

    document.querySelector(".btn-pay")?.addEventListener("click", function(){
        console.log("masuk querySelector");
        if(cart.length === 0){
            alert("Cart kosong")
            return
        }
        if(!merchantId){
            alert("Merchant belum dipilih")
        }
        if(!outletId){
            alert("Merchant belum dipilih")
        }
        console.log("merchantId=" + merchantId + " outletId=" + outletId );
        let total = cart.reduce((sum,i)=> sum + (i.qty*i.price),0)

        document.getElementById("paymentTotal").value =
            "Rp " + total.toLocaleString("id-ID")

        document.getElementById("cashAmount").value = total

        new bootstrap.Modal(
            document.getElementById("paymentModal")
        ).show()
    })
    document.getElementById("cashAmount")?.addEventListener("keyup", function(){
        let total = cart.reduce((sum,i)=> sum + (i.qty*i.price),0)
        let cash = parseFloat(this.value || 0)
        let change = cash - total
        document.getElementById("changeAmount").value =
            "Rp " + change.toLocaleString("id-ID")
    })
    document.getElementById("confirmPayment")?.addEventListener("click", function(){
        let paymentMethod =
            document.getElementById("paymentMethod").value
        let items = cart.map(i => ({
            productId: i.id,
            productName: i.name,
            qty: i.qty,
            price: i.price
        }))
        fetch("/api/sales",{
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify({
                merchantId: merchantId,
                outletId: outletId,
                paymentMethod: paymentMethod,
                items:items
            })
        })
            .then(()=>{
                alert("Payment success")

                cart=[]
                renderCart()

                bootstrap.Modal
                    .getInstance(document.getElementById("paymentModal"))
                    .hide()
            })
    })
});