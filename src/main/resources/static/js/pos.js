document.addEventListener("DOMContentLoaded", function () {
    let cart=[]
    document.querySelectorAll(".product-card")
        .forEach(card=>{
            card.addEventListener("click",()=>{
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
<div class="d-flex justify-content-between mb-2">
<span>${i.name} x${i.qty}</span>
<span>${formatMoney(subtotal)}</span>
</div>
`
        })
        document.getElementById("cartItems").innerHTML=html
        document.getElementById("total").innerText=formatMoney(total)
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
    document.getElementById("productSearch")?.focus();
});