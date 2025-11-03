(function ($) {
  $(function () {
    $(".products-container")
      .off("click.fly", ".btn-hover-add")
      .on("click.fly", ".btn-hover-add", function () {
        const $cartWrap = $("#openCart");
        const $badge = $("#cartCount");

        // card + id + imagen
        const $card = $(this).closest(".product");
        const pid = Number($card.data("pid"));
        const $img = $card.find(".img-wrapper img").eq(0);

        if (!pid || !$img.length || !$cartWrap.length) return;

        // agregar al carrito
        if (window.addToCartCore) window.addToCartCore(pid);

        // clonar imagen usando coordenadas de viewport
        const r = $img[0].getBoundingClientRect();
        const $clone = $img
          .clone()
          .css({
            position: "fixed",
            top: r.top,
            left: r.left,
            width: r.width,
            height: r.height,
            opacity: 0.9,
            "z-index": 1000,
            "border-radius": "12px",
            "box-shadow": "0 12px 24px rgba(0,0,0,.18)",
          })
          .appendTo(document.body);

        // destino: centro del contenedor del carrito
        const cr = $cartWrap[0].getBoundingClientRect();
        const targetTop = cr.top + cr.height / 2 - r.height / 4;
        const targetLeft = cr.left + cr.width / 2 - r.width / 4;

        // vuelo principal
        $clone.animate(
          {
            top: targetTop,
            left: targetLeft,
            width: r.width / 2,
            height: r.height / 2,
            opacity: 0.25,
          },
          1000,
          "easeInOutExpo",
          function () {
            // achicar y limpiar el clon
            $(this).animate(
              { width: 0, height: 0, opacity: 0 },
              150,
              function () {
                $(this).remove();
              }
            );

            // pop del numerito
            if ($badge.length) {
              $badge.removeClass("pop");
              void $badge[0].offsetWidth;
              $badge.addClass("pop");
              setTimeout(() => $badge.removeClass("pop"), 420);
            }
          }
        );
      });
  });
})(jQuery);
