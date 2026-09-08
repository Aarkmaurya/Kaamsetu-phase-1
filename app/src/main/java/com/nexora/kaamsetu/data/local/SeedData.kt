package com.nexora.kaamsetu.data.local

/**
 * One-time demo/mock data. This stands in for the future backend during the
 * test-APK phase (see MVP scope: mock/local data now, remote repository later).
 */
object SeedData {

    fun categories(): List<ServiceCategoryEntity> = listOf(
        ServiceCategoryEntity("electrician", "Home Repair", "Electrician"),
        ServiceCategoryEntity("plumber", "Home Repair", "Plumber"),
        ServiceCategoryEntity("carpenter", "Home Repair", "Carpenter"),
        ServiceCategoryEntity("painter", "Home Repair", "Painter"),
        ServiceCategoryEntity("mason", "Home Repair", "Mason"),
        ServiceCategoryEntity("welder", "Home Repair", "Welder"),
        ServiceCategoryEntity("ac_repair", "Appliance Repair", "AC Repair"),
        ServiceCategoryEntity("cooler_repair", "Appliance Repair", "Cooler Repair"),
        ServiceCategoryEntity("fridge_repair", "Appliance Repair", "Refrigerator Repair"),
        ServiceCategoryEntity("washing_machine_repair", "Appliance Repair", "Washing Machine Repair"),
        ServiceCategoryEntity("fan_repair", "Appliance Repair", "Fan Repair"),
        ServiceCategoryEntity("tv_repair", "Appliance Repair", "TV Repair"),
        ServiceCategoryEntity("mobile_repair", "Technology", "Mobile Repair"),
        ServiceCategoryEntity("computer_repair", "Technology", "Computer Repair"),
        ServiceCategoryEntity("wifi_setup", "Technology", "Wi-Fi Setup"),
        ServiceCategoryEntity("printer_repair", "Technology", "Printer Repair"),
        ServiceCategoryEntity("cctv_install", "Technology", "CCTV Installation"),
        ServiceCategoryEntity("home_cleaning", "Cleaning", "Home Cleaning"),
        ServiceCategoryEntity("bathroom_cleaning", "Cleaning", "Bathroom Cleaning"),
        ServiceCategoryEntity("sofa_cleaning", "Cleaning", "Sofa Cleaning"),
        ServiceCategoryEntity("water_tank_cleaning", "Cleaning", "Water Tank Cleaning"),
        ServiceCategoryEntity("bike_mechanic", "Vehicle", "Bike Mechanic"),
        ServiceCategoryEntity("car_mechanic", "Vehicle", "Car Mechanic")
    )

    fun technicians(): List<TechnicianEntity> = listOf(
        TechnicianEntity(
            id = "tech_1", name = "Ramesh Kumar", phone = "9990000001",
            status = "APPROVED", skillsCsv = "electrician,fan_repair", city = "Lucknow",
            rating = 4.6, completedJobs = 128, isVerified = true, isAvailable = true
        ),
        TechnicianEntity(
            id = "tech_2", name = "Suresh Yadav", phone = "9990000002",
            status = "APPROVED", skillsCsv = "plumber,water_tank_cleaning", city = "Lucknow",
            rating = 4.3, completedJobs = 76, isVerified = true, isAvailable = true
        ),
        TechnicianEntity(
            id = "tech_3", name = "Vikram Singh", phone = "9990000003",
            status = "PENDING", skillsCsv = "ac_repair,cooler_repair", city = "Kanpur",
            rating = 0.0, completedJobs = 0, isVerified = false, isAvailable = false
        ),
        TechnicianEntity(
            id = "tech_4", name = "Anil Sharma", phone = "9990000004",
            status = "APPROVED", skillsCsv = "mobile_repair,computer_repair", city = "Lucknow",
            rating = 4.8, completedJobs = 210, isVerified = true, isAvailable = true
        )
    )

    fun customers(): List<CustomerEntity> = listOf(
        CustomerEntity(id = "cust_demo", name = "Demo Customer", phone = "9998887770")
    )

    fun jobRequests(): List<JobRequestEntity> = listOf(
        JobRequestEntity(
            id = "job_1",
            customerId = "cust_demo",
            categoryId = "electrician",
            problemDescription = "Ceiling fan not switching on, possible wiring issue.",
            approximateArea = "Gomti Nagar, Lucknow",
            timing = "TODAY",
            preferredDateTime = "Today, evening",
            status = "QUOTES_RECEIVED",
            customerPhone = "9998887770",
            exactAddress = "House 12, Sector 4, Gomti Nagar, Lucknow"
        ),
        JobRequestEntity(
            id = "job_2",
            customerId = "cust_demo",
            categoryId = "plumber",
            problemDescription = "Kitchen tap leaking continuously.",
            approximateArea = "Indira Nagar, Lucknow",
            timing = "NOW",
            preferredDateTime = "As soon as possible",
            status = "REQUEST_CREATED",
            customerPhone = "9998887770",
            exactAddress = "Flat 302, Shanti Apartments, Indira Nagar, Lucknow"
        )
    )

    fun quotes(): List<QuoteEntity> = listOf(
        QuoteEntity(id = "quote_1", jobRequestId = "job_1", technicianId = "tech_1", price = 350.0, etaMinutes = 40),
        QuoteEntity(id = "quote_2", jobRequestId = "job_1", technicianId = "tech_4", price = 400.0, etaMinutes = 25)
    )
}
