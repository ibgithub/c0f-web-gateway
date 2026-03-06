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
    document.querySelectorAll(".category-btn")
        .forEach(btn => {

            btn.addEventListener("click",function(){

                document.querySelectorAll(".category-btn")
                    .forEach(b=>b.classList.remove("active"))

                this.classList.add("active")

                let category=this.dataset.category

                document.querySelectorAll(".product-card")
                    .forEach(card=>{

                        if(category==="all"){
                            card.style.display="block"
                        }else if(card.dataset.category===category){
                            card.style.display="block"
                        }else{
                            card.style.display="none"
                        }

                    })

            })

        })
});