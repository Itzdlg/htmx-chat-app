@import io.javalin.http.Context
@param Context ctx

<head>
    <script src="htmx.min.js"></script>
    <script src="tailwind.min.js"></script>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">

    <title>HTMX Practice App</title>
</head>
<body class="bg-gray-100">
    @template.components.nav()

    <h2 class="text-center text-3xl mb-5 mt-7">Login</h2>

    <div class="flex justify-center">
        <div class="w-full mx-3 max-w-sm">
            <div id="formError"></div>

            <form hx-post="login" hx-target="#formError" class="flex flex-col mx-3" hx-swap="innerHTML transition:true">
                <div>
                    <label for="username" class="block mb-2 text-sm font-medium text-gray-900">Username</label>
                    <input id="username" name="username" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5">
                </div>

                <div class="mt-6 mb-6">
                    <label for="password" class="block mb-2 text-sm font-medium text-gray-900">Password</label>
                    <input type="password" id="password" name="password" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5">
                </div>

                <button class="w-full h-12 rounded-xl bg-fuchsia-500 text-white font-semibold hover:bg-fuchsia-600">Login</button>
                <p class="my-2 text-center">or <a class="text-fuchsia-600 hover:underline hover:cursor-pointer" href="/register">register</a></p>
            </form>
        </div>
    </div>
</body>